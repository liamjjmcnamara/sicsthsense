package com.sics.sicsthsense.core;

import java.util.Set;
import java.util.UUID;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import com.sics.sicsthsense.model.openid.DiscoveryInformationMemento;
import com.sics.sicsthsense.model.security.*;


/**
 * <p>Simple representation of a User to provide the following to Resources:<br>
 * <ul>
 * <li>Storage of user state</li>
 * </ul>
 * </p>
 */
@JsonPropertyOrder({
  "id",
  "userName",
  "passwordDigest",
  "firstName",
  "lastName",
  "email",
  "openIDDiscoveryInformationMemento",
  "openIDIdentifier",
  "sessionToken",
  "authorities"
})
public class OpenIDUser {

  /** * <p>Unique identifier for this entity</p> */
  private String id;
  private String userName;

  /** * <p>A user password (not plaintext and optional for anonymity reasons)</p> */
  @JsonProperty
  protected String passwordDigest = null;

  /** * A first name */
  @JsonProperty
  private String firstName;

  /** * A last name */
  @JsonProperty
  private String lastName;

  /**
   * <p>The OpenID discovery information used in phase 1 of authenticating against an OpenID server</p>
   * <p>Once the OpenID identifier is in place, this can be safely deleted</p>
   */
  @JsonProperty
  private DiscoveryInformationMemento openIDDiscoveryInformationMemento;

  @JsonProperty
  private String openIDIdentifier;

  @JsonProperty
  private UUID sessionToken;

  /**The authorities for this User (an unauthenticated user has no authorities) */
  @JsonProperty
  private Set<Authority> authorities = Sets.newHashSet();

	@JsonProperty
	private String username;
	@JsonProperty
	public String email;
	@JsonProperty
	public String password; // only for username/password login
	@JsonProperty
	public String description = "";
	@JsonProperty
	public Double latitude = 0.0;
	@JsonProperty
	public Double longitude = 0.0;
	@JsonProperty
	public Date creationDate;
	@JsonProperty
	public Date lastLogin;
	@JsonProperty
	private String token; /** Secret token for authentication */
	@JsonProperty // only decoded if the poster is admin
	private boolean admin;

  @JsonCreator
  public OpenIDUser(
    @JsonProperty("id") String id,
    @JsonProperty("sessionToken") UUID sessionToken
  ) {

    this.id = id;
    this.sessionToken = sessionToken;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return The user name to authenticate with the client
   */
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * @return The digested password to provide authentication between the user and the client
   */
  public String getPasswordDigest() {
    return passwordDigest;
  }

  /**
   * <h3>Note that it is expected that Jasypt or similar is used prior to storage</h3>
   *
   * @param passwordDigest The password digest
   */
  public void setPasswordDigest(String passwordDigest) {
    this.passwordDigest = passwordDigest;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }


  /**
   * @return The user's email address
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   *
   * @return The OpenID discovery information (phase 1 of authentication)
   */
  public DiscoveryInformationMemento getOpenIDDiscoveryInformationMemento() {
    return openIDDiscoveryInformationMemento;
  }

  public void setOpenIDDiscoveryInformationMemento(DiscoveryInformationMemento openIDDiscoveryInformationMemento) {
    this.openIDDiscoveryInformationMemento = openIDDiscoveryInformationMemento;
  }

  /**
   * @return The OpenID identifier
   */
  public String getOpenIDIdentifier() {
    return openIDIdentifier;
  }

  public void setOpenIDIdentifier(String openIDIdentifier) {
    this.openIDIdentifier = openIDIdentifier;
  }

  /**
   * @return The session key
   */
  public UUID getSessionToken() {
    return sessionToken;
  }
  public void setSessionToken(UUID sessionToken) {
    this.sessionToken = sessionToken;
  }
	public boolean hasSessionToken() {
		return sessionToken!=null;
	}

  public void setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }

  public boolean hasAllAuthorities(Set<Authority> requiredAuthorities) {
    return authorities.containsAll(requiredAuthorities);
  }

  public boolean hasAuthority(Authority authority) {
    return hasAllAuthorities(Sets.newHashSet(authority));
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("userName", userName)
      .add("password", "**********")
      .add("email", email)
      .add("openIDIdentifier", openIDIdentifier)
      .add("sessionToken", sessionToken)
      .add("firstName", firstName)
      .add("lastName", lastName)
      .toString();
  }

}
