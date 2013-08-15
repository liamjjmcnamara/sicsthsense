package com.sics.sicsthsense.jdbi;

import java.util.List;

import org.skife.jdbi.v2.*;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.*;

import com.yammer.dropwizard.jdbi.*;
import com.yammer.dropwizard.db.*;

import com.sics.sicsthsense.core.*;

public interface StorageDAO {

  //@SqlUpdate("create table something (id int primary key, name varchar(100))")
  //void createSomethingTable();

	// Users
  @SqlQuery("select * from users where id = :id limit 1")
	@Mapper(UserMapper.class)
  User findUserById(@Bind("id") long id);

  @SqlQuery("select * from users where username = :username limit 1")
	@Mapper(UserMapper.class)
  User findUserByUsername(@Bind("username") String username);

  @SqlQuery("select * from users where email = :email limit 1")
	@Mapper(UserMapper.class)
  User findUserByEmail(@Bind("email") String email);

  @SqlQuery("select user_name from users where id = :id limit 1")
  String findUsernameById(@Bind("id") long id);

  @SqlUpdate("insert into users (id, name) values (:id, :name)")
  void insertUser(@Bind("id") long id, @Bind("name") String name);


	// Resources
  @SqlQuery("select * from resources where owner_id = :id")
	@Mapper(ResourceMapper.class)
  List<Resource> findResourcesByOwnerId(@Bind("id") long id);

  @SqlQuery("select * from resources where id = :id limit 1")
	@Mapper(ResourceMapper.class)
  Resource findResourceById(@Bind("id") long id);

  @SqlQuery("select * from resources where polling_period>0")
	@Mapper(ResourceMapper.class)
  List<Resource> findPolledResources();

  @SqlUpdate("insert into resources(owner_id, label, polling_period, last_polled, polling_url, polling_authentication_key, description, parent_id, secret_key, version, last_posted ) values (:owner_id, :label, :polling_period, :last_polled, :polling_url, :polling_authentication_key, :description, :parent_id, :secret_key, :version, :last_posted)")
  void insertResource(
		@Bind("label") String label,
		@Bind("version") String version,
		@Bind("owner_id")  long owner_id, 
		@Bind("parent_id") long parent_id,
		@Bind("polling_url") String polling_url,
		@Bind("polling_authentication_key") String polling_authentication_key,
		@Bind("polling_period") long polling_period,
		@Bind("secret_key")  String secret_key,
		@Bind("description") String description,
		@Bind("last_polled") long last_polled,
		@Bind("last_posted") long last_posted 
	);

  //@SqlUpdate("update resources set owner_id=':owner_id', label=':label', polling_period=':polling_period', last_polled=':last_polled', polling_url=':polling_url', polling_authentication_key=':polling_authentication_key', description=':description', parent_id=':parent_id', secret_key=':secret_key', version=':version', last_posted=':last_posted' where id  ':id'")
  @SqlUpdate("update resources set label = :label, polling_period=:polling_period, polling_url=:polling_url, polling_authentication_key=:polling_authentication_key where id = :id")
  void updateResource(
		@Bind("id") long id,
		@Bind("label") String label,
		@Bind("version") String version,
		@Bind("owner_id")  long owner_id, 
		@Bind("parent_id") Long parent_id,
		@Bind("polling_url") String polling_url,
		@Bind("polling_authentication_key") String polling_authentication_key,
		@Bind("polling_period") long polling_period,
		@Bind("secret_key")  String secret_key,
		@Bind("description") String description,
		@Bind("last_polled") long last_polled,
		@Bind("last_posted") long last_posted 
	);

  @SqlUpdate("delete from resources where id = :id")
  void deleteResource(@Bind("id") long id);

	// Streams
  @SqlQuery("select * from streams where resource_id = :resourceid")
	@Mapper(StreamMapper.class)
  List<Stream> findStreamsByResourceId(@Bind("resourceId") long resourceId);

  @SqlQuery("select * from streams where id = :id limit 1")
	@Mapper(StreamMapper.class)
  Stream findStreamById(@Bind("id") long id);

	// Parsers
  @SqlQuery("select * from parsers where id = :id limit 1")
	@Mapper(ParserMapper.class)
  Parser findParserById(@Bind("id") long id);

  @SqlQuery("select * from parsers where resource_id = :resourceId")
	@Mapper(ParserMapper.class)
  List<Parser> findParsersByResourceId(@Bind("resourceId") long resourceId);

	// DataPoints
  @SqlQuery("select * from data_point_double where stream_id = :stream_id limit 10")
	@Mapper(PointMapper.class)
  DataPoint findPointByStreamId(@Bind("stream_id") long stream_id);

  @SqlQuery("select * from data_point_double where stream_id = :stream_id limit :limit")
	@Mapper(PointMapper.class)
  List<DataPoint> findPointsByStreamId(@Bind("stream_id") long stream_id, @Bind("limit") int limit);

  @SqlUpdate("insert into data_point_double(id, stream_id, timestamp, data) values (:id, :stream_id, :timestamp, :data)")
  void insertDataPoint(@Bind("id") long id, @Bind("stream_id") long stream_id, @Bind("timestamp") long timestamp, @Bind("data") double data);



}
