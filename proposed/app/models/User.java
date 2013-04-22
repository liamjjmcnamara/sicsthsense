package models;

import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import org.apache.commons.codec.digest.DigestUtils;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.mvc.PathBindable;

import com.avaje.ebean.*;

//the table name "user" might be invalid for some db systems
@Entity
@Table(name = "users")
public class User extends Model implements Comparable<User> { //PathBindable<User>,
	/** User class, contains all personal information
	 */	
	private static final long serialVersionUID = 5178587449713353935L;

	@Id
	public Long id;

	@Column(length = 255, unique = true, nullable = false)
	@Constraints.MaxLength(255)
	@Formats.NonEmpty
	@Constraints.Email
	public String email;

	@Column(length = 255, unique = true, nullable = false)
	@Constraints.MaxLength(255)
	@Constraints.Required
	@Formats.NonEmpty
	public String userName;
	public String password; // only for username/password login
	public String firstName;
	public String lastName;
	public String location;

	@Column(nullable = false)
	public Date creationDate;
	public Date lastLogin;
	
	@OneToMany(mappedBy = "owner")
	public List<Resource> resourceList = new ArrayList<Resource>();
	@OneToMany(mappedBy = "owner")
	public List<Stream> streamList = new ArrayList<Stream>();
	@OneToMany(mappedBy = "owner")
	public List<Actuator> actuatorList = new ArrayList<Actuator>();
	@OneToMany(mappedBy = "owner")
	public List<Vfile> fileList = new ArrayList<Vfile>();
	
  @ManyToMany
  public List<Stream> followedStreams = new ArrayList<Stream>();
	
	/** Secret token for session authentication */
	@Transient
	public String currentSessionToken;

	/** Secret token for authentication */
	private String token;
	private boolean admin=false;

	
	public static Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);


	public User(String email, String userName, String firstName, String lastName,
			String location) {
		this.creationDate = new Date();
		this.email = email.toLowerCase();
		this.userName = userName.toLowerCase();
		this.firstName = firstName;
		this.lastName = lastName;
		this.location = location;
		this.password = hash(new BigInteger(130,new SecureRandom()).toString(32));
		this.admin = false;
	}
	public User() {
		this.creationDate = new Date();
		this.password = DigestUtils.md5Hex(new BigInteger(130,new SecureRandom()).toString(32));
		this.admin = false;
	}

	public String  getEmail()             { return email; }
	public void    setEmail(String email) { this.email=email; }
	public String  getToken()             { return token; }
	public String  getUserName()          { return userName; }
	public String  getFirstName()         { return firstName; }
	public String  getLastName()          { return lastName; }
	public String  getLocation()          { return location; }
	public Long    getId()                { return new Long(id); }
	public boolean exists()               { return exists(id); }
	public boolean isAdmin()		          { return admin; }
	public void		 setAdmin(boolean admin){ this.admin=admin; }

	public void updateUser(User user) {
		this.userName = user.userName.toLowerCase();
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.location = user.location;
		this.password = user.password;
		update();
	}

	public void setPassword(String newPassword) {
		this.password = DigestUtils.md5Hex(newPassword);
	}
	public String resetPassword() {
		String newPassword = new BigInteger(130,new SecureRandom()).toString(32);
		this.password = hash(newPassword);
		return newPassword;
	}

	public Date updateLastLogin() {
		this.lastLogin = new Date();
		update();
		return this.lastLogin;
	}
	public String generateToken() {
		token = UUID.randomUUID().toString();
		//save();
		return token;
	}
	//don't remove strange chars from username
	public void verify() {
//		userName = userName.replaceAll("[^\\w.-]", "");
	}

	public void save() {
		verify();
		super.save();
	}

	public void update() {
		verify();
		super.update();
	}
	
	public int compareTo(User user) {
		return token.compareTo(user.token);
	}
	
	public boolean equals(User user) {
		return user!=null && token.equals(user.token) && this.id == user.id;
	}

  public void followStream(Stream stream) {
    if(stream != null && stream.id > 0L && !followedStreams.contains(stream)) {
      followedStreams.add(stream);
    }
    this.saveManyToManyAssociations("followedStreams");
  }
  
  public void unfollowStream(Stream stream) {
    if(stream != null && stream.id > 0L) {
      followedStreams.remove(stream);
    }
    this.saveManyToManyAssociations("followedStreams");
  }
  
  public boolean isfollowingStream(Stream stream) {
   return (stream != null) && followedStreams.contains(stream);
  }
  
  public List<Stream> followedStreams() {
  	return followedStreams;
  }

	public void sortStreamList() {
		Logger.info("Sorting StreamList");
		Collections.sort(streamList);
	}

	// perform the Password -> Hash transform
	public static String hash(String toHash) {
		return DigestUtils.md5Hex(toHash);
	}
  
	public static User create(User user) {		
		user.generateToken();
		user.save();
		// is this necessary? -YES!
		user.saveManyToManyAssociations("followedStreams");
		return user;
	}

	public static User getByToken(String token) {
		if (token == null) { return null; }
		try {
			return find.where().eq("token", token).findUnique();
		} catch (Exception e) {
			return null;
		}
	}

	public static List<User> all() {
		return find.where().orderBy("userName asc").findList();
	}

	public static boolean exists(Long id) {
		return find.byId(id) != null;
	}

	public static User get(Long id) {
		return (id==null) ? null : find.byId(id);
	}
	
	public static User getByEmail(String email) {
		return find.where().eq("email", email).findUnique();
	}

	public static User getByUserName(String userName) {
		return find.where().eq("user_name", userName).findUnique();
	}

	public static void delete(Long id) {
		find.ref(id).delete();
	}
}
