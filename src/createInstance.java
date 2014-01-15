import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.CreateImageResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;

public class createInstance {
	public static AWSCredentials credentials;
	public static AmazonEC2Client amazonEC2Client;
	public static String Instance_ID = null;

	public static void main(String[] args) throws IOException {
		String sshKeyName = "Enter your sshKeyName";
		String securityGroupName = "Enter your securityGroupName";
		importCredentials();
		createEC2Object();

		System.out.println(" 281 LAB ASSIGNMENT NUMBER : 1");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		InputStreamReader inputstream = new InputStreamReader(System.in);
		BufferedReader bufferRead = new BufferedReader(inputstream);
		String takeinput = null;

		do {
			System.out
					.println("1 Create and Run Instance |2 Description of the Instances |  3 Stop Instance |4 Terminate ");
			System.out
					.println("5 Reboot Instance | 6 Create AMI Instance | 7 Start Instances | 8 Exit");
			System.out.println("Enter your choice :");
			int a = Integer.parseInt(scanner.next());

			switch (a) {
			case 1:
				runAWSInstance(sshKeyName, securityGroupName);
				break;
			case 2:
				describeInstance();
				break;
			case 3:
				stopAWSInstance();
				break;
			case 4:
				terminateInstance();
				break;
			case 5:
				rebootInstance();
				break;
			case 6:
				createAMInstances();
				break;
			case 7:
				startInstance();
				break;
			case 8:
				exit(0);

			default:
				break;

			}
			System.out.println("Do you want to continue y = yes | n = no");
			takeinput = bufferRead.readLine();

		} while (takeinput.equals(takeinput));

	}

	private static void exit(int i) {
		System.exit(0);

	}

	/**
	 * This method Runs the AWS Instance.
	 */

	private static void runAWSInstance(String keyName, String securityGroupName) {
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		runInstancesRequest.withImageId("ami-fe002cbb")
				.withInstanceType("t1.micro").withMinCount(1).withMaxCount(1)
				.withKeyName(keyName).withSecurityGroups(securityGroupName);
		RunInstancesResult runInstancesResult = amazonEC2Client
				.runInstances(runInstancesRequest);
		System.out
				.println("Result of starting instance: " + runInstancesResult);

	}

	/**
	 * This method Stops the Instance.
	 */

	private static void stopAWSInstance() {
		List<String> instancesToStop = new ArrayList<String>();
		instancesToStop.add(Instance_ID);
		StopInstancesRequest stopRequest = new StopInstancesRequest();
		stopRequest.setInstanceIds(instancesToStop);
		stopRequest.setRequestCredentials(credentials);
		amazonEC2Client.stopInstances(stopRequest);
		System.out.println("Instance with ID " + Instance_ID
				+ "has been Stopped");

	}

	/**
	 * This method terminates the Instance.
	 */
	private static void terminateInstance() {
		List<String> terminateInstanceList = new ArrayList<String>();
		terminateInstanceList.add(Instance_ID);
		TerminateInstancesRequest terminateInstanceRequest = new TerminateInstancesRequest();
		terminateInstanceRequest.setInstanceIds(terminateInstanceList);
		terminateInstanceRequest.setRequestCredentials(credentials);
		amazonEC2Client.terminateInstances(terminateInstanceRequest);
		System.out.println("Instance has been terminated!!");
	}

	/**
	 * This method restarts the Instance.
	 */
	private static void rebootInstance() {
		RebootInstancesRequest rebootInstanceRequest = new RebootInstancesRequest();
		rebootInstanceRequest.setRequestCredentials(credentials);
		List<String> restartInstanceList = new ArrayList<String>();
		restartInstanceList.add(Instance_ID);
		rebootInstanceRequest.setInstanceIds(restartInstanceList);
		amazonEC2Client.rebootInstances(rebootInstanceRequest);
		System.out.println("Instance has been rebooted again!!");
	}

	/**
	 * This method starts the Instance.
	 */
	private static void startInstance() {
		System.out.println("Starting Instance of ID " + Instance_ID);
		List<String> startInstanceList = new ArrayList<String>();
		startInstanceList.add(Instance_ID);
		StartInstancesRequest startInstanceRequest = new StartInstancesRequest();
		startInstanceRequest.setInstanceIds(startInstanceList);
		startInstanceRequest.setRequestCredentials(credentials);
		amazonEC2Client.startInstances(startInstanceRequest);
		System.out.println("Instance of ID " + Instance_ID + " has started!");
	}

	/**
	 * This method describes the Instance.
	 */
	private static void describeInstance() {
		DescribeInstancesResult dc = amazonEC2Client.describeInstances();
		List<Reservation> L = dc.getReservations();
		for (Reservation R : L) {
			List<Instance> L1 = R.getInstances();
			for (Instance I : L1) {
				if (I.getState().getCode() == 16) {
					Instance_ID = I.getInstanceId();

				}
				System.out
						.println("Present Instance ID  :" + I.getInstanceId());
				System.out.println("State of instances  :" + I.getState());
				System.out.println("Instance type  :" + I.getInstanceType());
				System.out.println("Instance Platform  :" + I.getPlatform());
				System.out.println("Image ID of instance  :" + I.getImageId());
				System.out.println("Subnet ID  :" + I.getSubnetId());

			}
		}
	}

	/**
	 * This method creates AMI Instance
	 * */
	private static void createAMInstances() throws IOException {
		InputStreamReader inputstream = new InputStreamReader(System.in);
		BufferedReader bfRead = new BufferedReader(inputstream);
		CreateImageRequest createImageRequest = new CreateImageRequest();
		String userInputName = null, description = null;
		createImageRequest.setRequestCredentials(credentials);
		createImageRequest.setInstanceId(Instance_ID);
		System.out.println("Please enter Name for respected  AMI:");
		userInputName = bfRead.readLine();
		createImageRequest.setName(userInputName);
		System.out.println("Please enter Description for respected AMI:");
		description = bfRead.readLine();
		createImageRequest.setDescription(description);
		CreateImageResult createImageResult = amazonEC2Client
				.createImage(createImageRequest);
		System.out.println("AMI with name " + userInputName + " and Image ID "
				+ createImageResult.getImageId()
				+ " has been created successfully");
	}

	/**
	 * Creates an "AmazonEC2Client" object that interacts with AWS to create
	 * instances.
	 * 
	 */

	private static void createEC2Object() {
		amazonEC2Client = new AmazonEC2Client(credentials);
		amazonEC2Client.setEndpoint("ec2.us-west-1.amazonaws.com");
	}

	/**
	 * Imports the credentials stored in "AwsCredentials.properties" file into a
	 * Java Object.
	 */

	private static void importCredentials() {
		try {
			credentials = new PropertiesCredentials(
					createInstance.class
							.getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}