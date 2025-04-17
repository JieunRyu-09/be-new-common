package kr.co.triphos.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class WebhookDTO {

	private String secret;
	private String ref;
	private String before;
	private String after;
	private String compareUrl;
	private List<Commit> commits;
	private Repository repository;
	private Pusher pusher;
	private Sender sender;

	// Getters and Setters

	public static class Commit {
		private String id;
		private String message;
		private String url;
		private Author author;
		private Author committer;
		private String timestamp;

		// Getters and Setters
	}

	public static class Author {
		private String name;
		private String email;
		private String username;

		// Getters and Setters
	}

	public static class Repository {
		private int id;
		private Owner owner;
		private String name;
		private String fullName;
		private String description;
		private boolean privateRepo;
		private boolean fork;
		private String htmlUrl;
		private String sshUrl;
		private String cloneUrl;
		private String website;
		private int starsCount;
		private int forksCount;
		private int watchersCount;
		private int openIssuesCount;
		private String defaultBranch;
		private String createdAt;
		private String updatedAt;

		// Getters and Setters
	}

	public static class Owner {
		private int id;
		private String login;
		private String fullName;
		private String email;
		private String avatarUrl;
		private String username;

		// Getters and Setters
	}

	public static class Pusher {
		private int id;
		private String login;
		private String fullName;
		private String email;
		private String avatarUrl;
		private String username;

		// Getters and Setters
	}

	public static class Sender {
		private int id;
		private String login;
		private String fullName;
		private String email;
		private String avatarUrl;
		private String username;

		// Getters and Setters
	}
}
