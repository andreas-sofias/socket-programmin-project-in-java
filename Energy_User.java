import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Energy_User{

	public final static int PORT = 43210;
    private static ArrayList<Users> online = new ArrayList<>();
	public static void main(String[] args) {

		//In order to use terminal, use the following code for each user
		String username = args[0];
		int password = Integer.parseInt(args[1]);
		int energy = Integer.parseInt(args[2]);
        int necessary_energy = Integer.parseInt(args[3]);
		new Energy_User().startClient(username, password, energy, necessary_energy);

		//In order to use IDE, use the following code for each user
		//new Energy_User().startClient("Vi", 8080, 300);
	}

	private void startClient(String username, int password, int energy, int necessary_energy) {

		Socket requestSocket = null;
		ObjectOutputStream out;
		ObjectInputStream in;

		try {

			requestSocket = new Socket(InetAddress.getByName("192.168.1.12"), PORT);
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			in = new ObjectInputStream(requestSocket.getInputStream());


			/*This is the validation process for each
			 * user that connects. The data contains the
			 * username and the password. The user receive
			 * 'yes' or 'no' string from the server. If it is yes
			 * he continues and send the energy he requires,
			 * otherwise the connection is over */
			String data = username + " " + password;
			out.writeObject(data);
			out.flush();
			String result = (String)in.readObject();

			if (result.equals("no")){
				System.out.println("You are not enrolled for this process\n....");
				requestSocket.close();
				throw new IOException("");

			}else if (result.equals("yes")){
				System.out.println("You are connected!");
				out.writeObject(energy);
				out.flush();
				out.writeObject(necessary_energy);
				out.flush();
			}



			/* This is the process where the User has to choose the amount
			* of energy that he needs. He can choose if he wants it now
			* or if he wants to wait for a certain time. */
			Scanner obj = new Scanner(System.in);

			boolean flag = true;
			while(flag) {
				System.out.println("Choose one of the following options:\n1 - Retrieve energy from one or more producers\n2 - Save the request until later\n3 - Exit\n4 - Online Users");
				int answer = obj.nextInt();
				out.writeObject(answer);

				if (answer == 1) {
					System.out.println("Enter the amount of energy you want:");
					int amount_energy = obj.nextInt();
					out.writeObject(amount_energy);
					out.flush();

					Object info = in.readObject();
					if (info instanceof Users) {
						Users icast = (Users) info;
						System.out.println("This user can provide you: " + icast.getUsername() + "\nHe can provide you: " + icast.getEnergy() + "KWhs");

					} else {
						String icast = (String) info;
						if (icast.equals("empty")) {
							System.out.println("There are no available users for exchange!");
							System.out.println("Cannot proceed...\n..");
						}
						flag = false;
					}

				} else if (answer == 2) {
					System.out.println("For how long would you like to save your request? (Enter seconds)");
					int seconds = obj.nextInt() * 1000;
					out.writeObject(seconds);
					out.flush();

				} else if (answer == 3) {
					flag = false;
					System.out.println("Thank you!");
					requestSocket.close();
				}else if (answer == 4){

					online = (ArrayList<Users>)in.readObject();
					for (int i = 0; i < online.size(); i++) {
						System.out.println(online.get(i).getUsername());
					}

				} else {
					System.out.println("Your input was not correct! Please try again..\n..");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try{
			requestSocket.close(); //close connection when you are done sending all the data.
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}


    	
    	
	

