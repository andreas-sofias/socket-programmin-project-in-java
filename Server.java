import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Server {

	public static void main(String[] args) {

		new Server().openServer();

	}

	private HashMap<String, Integer> validation = new HashMap<>();
	private ArrayList<Users> online = new ArrayList<>();
	private final ArrayList<Users> availableExchange = new ArrayList<>();
	private Random rand;
	private Users user;

	public void openServer() {


		validation = ReadUsers.UserValidation("C:\\Users\\allys\\Desktop\\random\\Energy\\src\\users.txt");
		System.out.println("Server is working..");
		System.out.println();


		ServerSocket providerSocket = null;
		Socket connection;
		ObjectOutputStream out;
		ObjectInputStream in;
		String message = null;


		try {

			providerSocket = new ServerSocket(43210);

			while (true){

				connection = providerSocket.accept();
				System.out.println("Connection is established...");
				System.out.println("----------");

				out = new ObjectOutputStream(connection.getOutputStream());
				in = new ObjectInputStream(connection.getInputStream());

				try{
					message = (String)in.readObject();
				}catch(ClassNotFoundException e){
					e.printStackTrace();
				}

				//Verification begins
				user = verification(message, out, in, connection);


				ClientHandler t = new ClientHandler(user,connection,in,out);
				t.start();


			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				providerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}


	/* The ClientHandler is responsible for each user */
	class ClientHandler extends Thread
	{

		final ObjectInputStream in;
		final ObjectOutputStream out;
		final Socket s;
		final Users user;
		int mode;


		public ClientHandler(Users user, Socket s, ObjectInputStream in, ObjectOutputStream out)
		{
			this.user = user;
			this.s = s;
			this.in = in;
			this.out = out;

		}

		@Override
		public void run()
		{

			try {
                online.add(user);

				while (online.isEmpty()) {
					// Wait and try again afterwards
					Thread.sleep(100);
				}

				boolean flag = true;

				while(flag) {
					// This is the mode that the user chose
					mode = (int) in.readObject();

					if (mode == 1) {

						energyRequest(out, in);

					} else if (mode == 2) {
						int time = (Integer) in.readObject();
						System.out.println(time + " from server");
						try {
							Thread.sleep(time);
						} catch (InterruptedException e) {
							System.out.println("Interrupted thread");
							energyRequest(out, in);
						}
					}else if (mode == 4){
						out.writeObject(online);
						out.flush();
						out.reset();

					} else {
                        for (int i = 0; i < online.size(); i++) {
                            if( this.user.getPassword() == online.get(i).getPassword() ){
                                System.out.println("the is removed" + online.get(i).getUsername());
                                online.remove(i);
                            }
                        }
						flag = false;
						System.out.println("User " + this.user.getUsername() + " logged out!");
					}
				}


                for (int i = 0; i < online.size(); i++) {
                    System.out.println(online.get(i).getUsername());
                }
				s.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

    /*This method returns a random User from the list
    * that is given. */
	private Users randomItem(ArrayList<Users> mylist) {
		rand = new Random();
		return mylist.get(rand.nextInt(mylist.size()));
	}

	/*This synchronized method checks the username and the password of
	* each user. The server has a HashMap with all the enrolled users.
	* So if an user tries to connect the server will search for
	* his data in the HashMap. If he exist and the data are correct the
	* server sends a yes as a response and creates an object for the User
	* . If he doesn't exist he send a no */
	private synchronized Users verification(String message, ObjectOutputStream out, ObjectInputStream in, Socket connection){


		try {
			String name = message.split(" ")[0];
			int pass = Integer.parseInt(message.split(" ")[1]);

			if (validation.containsKey(name)  &&  validation.get(name) == pass) {
				out.writeObject("yes");
				out.flush();

				int energy_req = (Integer) in.readObject();
				int energy_inNeed = (Integer) in.readObject();
				String ip = connection.getLocalAddress().getHostAddress();
				user = new Users(ip, name, pass, energy_req, energy_inNeed);

				return user;

			} else {
				out.writeObject("no");
				out.flush();
			}

		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}

		return null;
	}

	/* This synchronized method executes the energy distribution
	 * The servers receives the amount of energy the user wants and
	 * make a search between all the online users. For each of them the
	 * server checks if they have enough amount of energy to share. If yes
	 * he randomly choose a user an proceeds with the energy distribution. If
	 * by the he didn't found anyone, he sends a message to the User */
	private synchronized void energyRequest( ObjectOutputStream out, ObjectInputStream in) {

		int message;

		try {

			message = (int) in.readObject();
			System.out.println("The user " + this.user.getUsername() + " needs " + message + "KWhs..");

			for (Users live : online) {

				if (!(live.getPassword() == user.getPassword())) {

					if (live.getEnergy() >= message) {

						System.out.println(live.getUsername() + " has enough energy available..");

						availableExchange.add(live);

					}
				}
			}

			if (availableExchange.isEmpty()) {
				out.writeObject("empty");
				out.flush();
				out.reset();
			} else {
				Users randomElement = randomItem(availableExchange);
				out.writeObject(randomElement);
				out.flush();
				out.reset();

				this.user.increaseEnergy(message);
				randomElement.decreaseEnergy(message);

				//Loading effect
				System.out.println();
				for (int i = 0; i < 90; i += 14) {

					Thread.sleep(600);
					System.out.println(i + "%");

				}
				System.out.println("\nUpdated..");
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
