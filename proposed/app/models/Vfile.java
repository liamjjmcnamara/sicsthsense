/*
 * Copyright (c) 2013, Swedish Institute of Computer Science
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of The Swedish Institute of Computer Science nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

/* Description:
 * TODO:
 * */

package models;

import java.lang.Long;
import java.util.*;

import javax.persistence.*;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.EnumValue;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

import play.libs.F.*;
import play.libs.WS;

import controllers.Utils;

@Entity
@Table(name = "vfiles", uniqueConstraints = { 
		@UniqueConstraint(columnNames = {"owner_id", "path" }) 
		})
public class Vfile extends Model {

	public Vfile() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1766439519493690841L;

	public static enum Filetype {
		@EnumValue("F")
		FILE, 
		@EnumValue("D")
		DIR
		};

	/* should this be a UUID instead?*/
	@Id
	public Long id;

	@Column(nullable = false)
	@Constraints.Required
	String path;

	@Column(name = "owner_id", nullable = false)
	@Constraints.Required
	@ManyToOne
	User owner;

	public User getOwner() {
		return owner;
	}
	
	@Column(nullable = false)
	@Constraints.Required
	Filetype type;

	//@Column(nullable = false)
	//@Constraints.Required
	@OneToOne(cascade = CascadeType.ALL)
	Stream linkedStream;

	public static Finder<Long,Vfile> find = new Finder<Long,Vfile>(Long.class, Vfile.class); 

	public Vfile(User user, String path, Filetype type) {
		this.owner = user;
		this.path= path;
		this.type = type;
	}
	
	public static Vfile create(Vfile file) {
		if (file.owner != null) {
			file.save();
			return file;
		}
		Logger.error("Could not create file because owner is null: " + file.path);
		return null;
	}
	
	public Filetype getType() {
		return type;
	}

	public boolean isFile() {return type==Filetype.FILE;}
	public boolean isDir() {return type==Filetype.DIR;}
	
	public String getPath() {
		return path;
	}
	
	public String getName() {
		if(path == null) {
			return null;
		}
		String [] subPaths = path.split("/");
		int i = subPaths.length-1;
		if(i<0) return "";
		return subPaths[ i ];
	}
	
	public String getParentPath() {
		if(path == null) {
			return null;
		}
		int i = path.lastIndexOf("/"+getName());
		if(i<0) return "";
		return path.substring(0, i);
	}
	
/*	public String getUpperLevel() {
		if (path == null) {
			return null;
		}
		int i = path.lastIndexOf("/" + getName());
		if (i <= 0) {
			return "";
		} else {
			int j = path.substring(0, i - 1).lastIndexOf('/');
			if (j <= 0) {
				return "";
			} else {
				return path.substring(0, j);
			}	
		}
	}*/
	
	public void setLink(Stream linkedStream) {
		this.linkedStream = linkedStream;
		if(id != null) {
			this.update();
		}
	}
	public Stream getLink() {
		return linkedStream;
	}
	
	public void delete() {
		//TODO: Check dependencies
		//this.linkedStream.file = null;
		super.delete();
	}

	public static String extractUpperLevelPath(String path) {
		if (path == null) {
			return "";
		}
		if(path.endsWith("/")) {
			path = path.substring(0, path.length()-1);
		}
		String[] subPaths = path.split("/");
		if(subPaths.length <= 0) {
			return "";
		}
		String name = subPaths[subPaths.length - 1];
		int i = path.lastIndexOf("/" + name);
		if (i <= 0) {
			return "";
		} else {
			return path.substring(0, i);
		}
	}

	public static String extractParentUpperLevelPath(String path) {
		int j = extractUpperLevelPath(path).lastIndexOf('/');
		if (j < 0) {
			return "";
		} else {
			return path.substring(0, j);
		}
	}
}
