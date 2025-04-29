package kr.co.triphos.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class GitTeaWebhookDTO extends WebhookDTO{

	private String secret;
	private String ref;
	private String before;
	private String after;
	private String compareUrl;
	private List<WebhookCommit> commits;
	private WebhookRepository repository;
	private WebhookPusher pusher;
	private WebhookSender sender;
	private int totalCommits;

	@Data
	public static class WebhookCommit {
		private String id;
		private String message;
		private String url;
		private WebhookAuthor author;
		private WebhookAuthor committer;
		private String timestamp;
	}

	@Data
	public static class WebhookAuthor {
		private String name;
		private String email;
		private String username;

	}

	@Data
	public static class WebhookRepository {
		private int id;
		private WebhookOwner owner;
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
	}

	@Data
	public static class WebhookOwner {
		private int id;
		private String login;
		private String fullName;
		private String email;
		private String avatarUrl;
		private String username;
	}

	@Data
	public static class WebhookPusher {
		private int id;
		private String login;
		private String fullName;
		private String email;
		private String avatarUrl;
		private String username;
	}

	@Data
	public static class WebhookSender {
		private int id;
		private String login;
		private String fullName;
		private String email;
		private String avatarUrl;
		private String username;
	}
}
