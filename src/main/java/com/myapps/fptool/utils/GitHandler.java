package com.myapps.fptool.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class GitHandler {

	private String gitUrl;
	private String gitBranch;
	private String tempDir;
	private boolean isWindows;

	public GitHandler(String gitUrl, String gitBranch, String tempDir) {
		super();
		this.gitUrl = gitUrl;
		this.gitBranch = gitBranch;
		this.tempDir = tempDir;
		this.isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	}

	public String getFileFromGit(String filePath) {

		if (this.isWindows)
			return null;
		else
			return getFileFromGitOnLinux(filePath);

	}

	private String getFileFromGitOnLinux(String filePath) {
		String pathToFile = null;
		System.out.println("Begining getFileFromGitOnLinux");
		try {

			String gitSshUrl = gitUrl.replaceFirst("https://github.com/", "git@github.com:");
			String tempGitDir = "tempgit";
			String repoName = gitSshUrl.substring(gitSshUrl.indexOf("/") + 1, gitSshUrl.lastIndexOf("."));

			String commandString = getCommandString(tempGitDir, gitSshUrl, repoName, filePath);

			/*
			 * ProcessBuilder builder = new ProcessBuilder();
			 * builder.command(commandString);
			 * 
			 * //builder.directory(new File(System.getProperty("user.home"))); Process
			 * process = builder.start(); StreamGobbler streamGobbler = new
			 * StreamGobbler(process.getInputStream(), System.out::println);
			 * Executors.newSingleThreadExecutor().submit(streamGobbler); int exitCode =
			 * process.waitFor(); assert exitCode == 0;
			 */

			System.out.println("Your git file has been retrieved!");
			pathToFile = this.tempDir.concat(filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()));

			System.out.println("gitUrl : " + this.gitUrl);
			System.out.println("gitBranch : " + this.gitBranch);
			System.out.println("tempDir : " + this.tempDir);
			System.out.println("isWindows : " + this.isWindows);
			System.out.println("gitSshUrl = " + gitSshUrl);
			System.out.println("tempGitDir = " + tempGitDir);
			System.out.println("repoName = " + repoName);

			System.out.println("commandString = " + commandString);
			System.out.println("pathToFile = " + pathToFile);

			System.out.println("Ending getFileFromGitOnLinux");

		} catch (Exception ex) { // | IOException | InterruptedException ex) {
			ex.printStackTrace();
		}
		return pathToFile;
	}

	private String getCommandString(String tempGitDir, String gitSshUrl, String repoName, String filePath) {

		StringBuilder commandString = new StringBuilder();
		commandString.append("/bin/sh -c ");
		commandString.append("cd " + this.tempDir + " && "); // cd temp
		commandString.append("mkdir " + tempGitDir + " && ");// mkdir tempgit
		commandString.append("cd " + tempGitDir + " && ");// cd tempgit
		commandString.append("git clone -n " + gitSshUrl + " --depth 1 " + " && "); // git clone -n
																					// git@github.com:trilogy-group/ignite-firstrain-orion.git
																					// --depth 1
		commandString.append("cd " + repoName + " && "); // cd ignite-firstrain-orion
		commandString.append("git checkout " + this.gitBranch + " " + filePath + " && "); // git checkout HEAD
																							// CustomerEngagementProcesses/src/main/java/com/firstrain/intercom/processors/IntercomInactivateUserProcessor.java
		commandString.append("mv " + filePath + " ../../" + " && ");// mv
																	// CustomerEngagementProcesses/src/main/java/com/firstrain/intercom/processors/IntercomInactivateUserProcessor.java
																	// ../../
		commandString.append("cd ../../ && "); // cd ../../
		// commandString.append("rm -rf " + tempGitDir +" && "); // rm -rf tempgit
		// /@TODO - commenting rm -rf - dangerous.
		commandString.append("cd .. "); // cd ..

		// System.out.println("commandString = " + commandString.toString());

		return commandString.toString();

	}

	private static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private Consumer<String> consumer;

		public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Begining Git Test");

		String gitUrl = "https://github.com/trilogy-group/ignite-firstrain-orion.git";
		String gitBranch = "HEAD";
		String tempDir = "/home/mint/t/";
		String filePath = "CustomerEngagementProcesses/src/main/java/com/firstrain/intercom/processors/IntercomInactivateUserProcessor.java";
		String returnPath;

		System.out.println("Creating Git Handler");
		GitHandler gitHandler = new GitHandler(gitUrl, gitBranch, tempDir);
		System.out.println("Calling gitHandler.getFileFromGit");
		returnPath = gitHandler.getFileFromGit(filePath);
		System.out.println("returnPath = " + returnPath);

		System.out.println("Ending Git Test");
	}

}