package com.master.spring.boot.blog.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.NotEmpty;

import com.github.rjeschke.txtmark.Processor;
import com.master.spring.boot.blog.domain.es.Esblog;

/**
 * Blog 实体
 * 
 * @since 1.0.0 2017年4月7日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Entity // 实体
public class Blog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id // 主键
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
	private Long id; // 用户的唯一标识
	
	@NotEmpty(message = "标题不能为空")
	@Size(min=2, max=50)
	@Column(nullable = false, length = 50) // 映射为字段，值不能为空
	private String title;
	
	@NotEmpty(message = "摘要不能为空")
	@Size(min=2, max=300)
	@Column(nullable = false) // 映射为字段，值不能为空
	private String summary;

	@Lob  // 大对象，映射 MySQL 的 Long Text 类型
	@Basic(fetch=FetchType.LAZY) // 懒加载
	@NotEmpty(message = "内容不能为空")
	@Size(min=2)
	@Column(nullable = false) // 映射为字段，值不能为空
	private String content;
	
	@Lob  // 大对象，映射 MySQL 的 Long Text 类型
	@Basic(fetch=FetchType.LAZY) // 懒加载
	@NotEmpty(message = "内容不能为空")
	@Size(min=2)
	@Column(nullable = false) // 映射为字段，值不能为空
	private String htmlContent; // 将 md 转为 html
 	
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(nullable = false) // 映射为字段，值不能为空
	@CreationTimestamp  // 由数据库自动创建时间
	private Timestamp createTime;

	@Column(name="readSize")
	private Integer readSize = 0; // 访问量、阅读量
	 
	@Column(name="commentSize")
	private Integer commentSize = 0;  // 评论量

	@Column(name="voteSize")
	private Integer voteSize = 0;  // 点赞量
	
	@Column(name="tags", length = 100) 
	private String tags;  // 标签
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "blog_comment", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"), 
	    inverseJoinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id"))
	private List<Comment> comments;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "blog_vote", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"), 
	    inverseJoinColumns = @JoinColumn(name = "vote_id", referencedColumnName = "id"))
	private List<Vote> votes;
	
	@OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(name = "catalog_id")
	private Catalog catalog;
	
	protected Blog() {
	}
	public Blog(String title, String summary,String content) {
		this.title = title;
		this.summary = summary;
		this.content = content;
	}
	public Blog(Esblog esblog) {
		this.id = esblog.getBlogId();
		this.title = esblog.getTitle();
		this.summary = esblog.getSummary();
		this.content = esblog.getContent();
		this.htmlContent = Processor.process(esblog.getContent());
		this.user.setUsername(esblog.getUsername());
		this.user.setAvatar(esblog.getAvatar());;
		this.createTime = esblog.getCreateTime();
		this.readSize = esblog.getReadSize();
		this.commentSize = esblog.getCommentSize();
		this.voteSize = esblog.getVoteSize();
		this.tags = esblog.getTags();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		this.htmlContent = Processor.process(content); // 将Markdown 内容转为 HTML 格式.
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
 
	public Timestamp getCreateTime() {
		return createTime;
	}
	
	public String getHtmlContent() {
		return htmlContent;
	}
	public Integer getReadSize() {
		return readSize;
	}
	public void setReadSize(Integer readSize) {
		this.readSize = readSize;
	}
	public Integer getCommentSize() {
		return commentSize;
	}

	public Integer getVoteSize() {
		return voteSize;
	}
	public void setVoteSize(Integer voteSize) {
		this.voteSize = voteSize;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
		this.commentSize = this.comments.size();
	}
	/**
	 * 添加评论
	 * @param comment
	 */
	public void addComment(Comment comment) {
		this.comments.add(comment);
		this.commentSize = this.comments.size();
	}
	
	/**
	 * 删除评论
	 * @param commentId
	 */
	public void removeComment(Long commentId) {
		
		for (Comment comment : comments) {
			if(comment.getId() == commentId){
				this.comments.remove(comment);
				break;
			}
		}
		this.commentSize = this.comments.size();	
	}

	
	//点赞相关
	public List<Vote> getVotes() {
		return votes;
	}
	public void setVotes(List<Vote> votes) {
		this.votes = votes;
		this.voteSize = votes.size();
	}
	
	
	/**
	 * 点赞
	 * @param vote
	 */
	public boolean addVote(Vote vote) {
		//标识该用户是否已经点过赞
//		boolean isExist = false;
		if(!votes.contains(vote)){
			this.votes.add(vote);
			this.voteSize = this.votes.size();
			return false;
		}
		return true;
	}
	
	/**
	 * 取消点赞
	 * @param commentId
	 */
	public void removeVote(Long voteId) {
		
		for (Vote vote : votes) {
			if(vote.getId() == voteId){
				this.votes.remove(vote);
				break;
			}
		}
		this.voteSize = this.votes.size();	
	}
	
	public Catalog getCatalog() {
		return catalog;
	}
	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}

}
