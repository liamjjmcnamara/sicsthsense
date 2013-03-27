package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import controllers.Utils;

import javax.persistence.*;

import com.avaje.ebean.*;
import com.avaje.ebean.annotation.EnumValue;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.db.ebean.*;
import play.Logger;
import play.libs.*;
import play.libs.WS.WSRequestHolder;
import play.mvc.Result;
import play.mvc.Http.Request;


@Entity
@Table(name = "parsers")
public class StreamParser extends Model {
	
	/**
	 * The serialization runtime associates with each serializable class a version
	 * number, called a serialVersionUID
	 */
	private static final long serialVersionUID = 2972078391685154152L;
//	public static enum parserType {
//		@EnumValue("R")
//		REGEX,
//		@EnumValue("J")
//		JSON,
//		@EnumValue("X")
//		XPATH
//	};

	@Id
	public Long id;

	@ManyToOne
	public Source source;

	@ManyToOne
	public Stream stream;

	@Transient
	public String streamVfilePath;
	
	/** RegEx, Xpath, JSON path */
	public String inputParser;

	/**
	 * JSON, HTML, text, XML, ... to override MIME contentType of input Right now,
	 * it could be defined as application/json, otherwise, request's content is
	 * handled as text
	 */
	public String inputType = null;

	// Format of the posted time string to be parsed to UNIX time = 
	//	"Thu Sep 28 20:29:30 JST 2000";
	//	"EEE MMM dd kk:mm:ss z yyyy"; 
	// or "" or "unix" for unix timestamp
	public String timeformat = null;
	    
	@Transient
	public Pattern regexPattern;

	public static Model.Finder<Long, StreamParser> find = new Model.Finder<Long, StreamParser>(Long.class, StreamParser.class);

	public StreamParser() {
		super();
	}

	public StreamParser(Source source, String inputParser, String inputType, String path, String timeformat) {
		super();
		setInputParser(inputParser);
		this.inputType = inputType;
		this.source = source;
		this.streamVfilePath = path;
		this.timeformat = timeformat;
		Vfile f = FileSystem.readFile(source.owner, path);
		this.stream = (f != null) ? f.linkedStream : null;
		//getOrCreateStreamFile(path).linkedStream;
	}
	
//	public StreamParser(Source source, String inputParser, String inputType, String path) {
//		this(source, inputParser, inputType, path, "");
//	}
	
	public StreamParser(Source source, String inputParser, String inputType, Stream stream, String timeformat) {
		super();
		setInputParser(inputParser);
		this.inputType = inputType;
		this.source = source;
		this.stream = stream;
		this.timeformat = timeformat;
		if(stream != null && stream.file != null) {
			this.streamVfilePath = stream.file.path;
		}
	}
	
//	public StreamParser(Source source, String inputParser, String inputType, Stream stream) {
//		this(source, inputParser, inputType, stream, "");
//	}


	public boolean setInputParser(String inputParser) {
		this.inputParser = inputParser;
		if (inputParser != null) {
			regexPattern = Pattern.compile(inputParser);
			if (this.id != null) {
				this.update();
			}
			return true;
		}
		return false;
	}

	public boolean parseRequest(Request request, Long currentTime) {
		try {
			if ("application/json".equalsIgnoreCase(inputType)
					|| "application/json".equalsIgnoreCase(request.getHeader("Content-Type")) ) {
				JsonNode jsonBody = request.body().asJson();
				//Logger.info("[StreamParser] as json");
				return parseJsonResponse(jsonBody, currentTime);
			} else {
				String textBody = request.body().asText(); //request.body().asRaw().toString();
				//Logger.info("[StreamParser] as text");
				return parseTextResponse(textBody, currentTime);
			}
		} catch (Exception e) {
			Logger.info("[StreamParser] Exception " + e.getMessage() + e.getStackTrace()[0].toString());
		}
		return false;
	}

	/**
	 * parseResponse(Request req) chooses the parser based on content-type.
	 * inputType overrides the content-type. returns: true if could post
	 */
	public boolean parseResponse(WS.Response response, Long currentTime) {
		Logger.info("StreamParser: parseResponse() "+stream.file.getPath());
		try {
			if ("application/json".equalsIgnoreCase(inputType)
					|| "application/json".equalsIgnoreCase(response.getHeader("Content-Type")) ) {
				JsonNode jsonBody = response.asJson();
				return parseJsonResponse(jsonBody, currentTime);
			} else {
				String textBody = response.getBody();
				return parseTextResponse(textBody, currentTime);
			}
		} catch (Exception e) {
			Logger.info("[StreamParser] Exception " + e.getMessage() + e.getStackTrace()[0].toString());
		}
		return false;
	}

/**
 * Parses the request using inputParser as regex and posts the first match
 *  
 * @param textBody
 * @return true if could post
 */
	private boolean parseTextResponse(String textBody, Long currentTime) {
		try {
			double number = 0.0;
			String value = "", time = "";
			if (stream != null && inputParser != null
					&& !inputParser.equalsIgnoreCase("")) {
				regexPattern = Pattern.compile(inputParser);
				Matcher matcher = regexPattern.matcher(textBody);
				if (textBody != null && matcher.find()) {
					
					// if(matcher.groupCount()>1)
					// for (int i = 1; i <= matcher.groupCount(); i += 2) {
					// value = matcher.group(i);
					// number = Double.parseDouble(value);
					// if (matcher.groupCount() > i) {
					// time = matcher.group(i + 1);
					// if(timeformat != null && !"".equalsIgnoreCase(timeformat) &&
					// !"unix".equalsIgnoreCase(timeformat)){
					// //inputParser REGEX should match the whole date/time string! It is
					// not enough to provide the time format only!
					// currentTime = parseDateTime(time);
					// } else {
					// currentTime = Long.parseLong(time);
					// }
					// }
					// }
					
					//try to match value from the group called :value: otherwise, use the first matching group
					value = matcher.group("value");					
					if (value == null) {
						value = matcher.group(1);
					}
					number = Double.parseDouble(value);

					//try to match time from the group called :time: otherwise, use the second matching group
					time = matcher.group("time");
					if (time == null) {
						time = matcher.group(2);
					}
					//if there is a match for time, parse it; otherwise, use the system time (provided in the parameter currentTime)
					if (time != null) {
						if (timeformat != null && !"".equalsIgnoreCase(timeformat)
								&& !"unix".equalsIgnoreCase(timeformat)) {
							// inputParser REGEX should match the whole date/time string! It is
							// not enough to provide the time format only!
							currentTime = parseDateTime(time);
						} else {
							currentTime = Long.parseLong(time);
						}
					}
				}
			} else {
				number = Double.parseDouble(textBody);
			}
			return stream.post(number, currentTime);
		} catch (NumberFormatException e) {
			Logger.warn("StreamParser: Regex failed to parse a number!");
			// naughty rogue value
			//return stream.post(-1, currentTime);
		} catch (Exception e) {
			Logger.error("StreamParser: Regex failed!");
		}
		return false;
	}

	private Long parseDateTime(String input) {
    DateFormat df = new SimpleDateFormat(timeformat, Locale.ENGLISH);
    Long result = -1L;
    try {
			result =  df.parse(input).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.info("[StreamParser] Exception " + e.getMessage() + e.getStackTrace()[0].toString());
			Logger.info("[StreamParser] Exception timeformat: " + timeformat + "input: " + input);

		}
		return result;  
	}
	
//	private boolean parseCSVdata(String data, String seperator, String timeformat, int dataIndex, int timeIndex) {
//		String [] fields = data.split(seperator);
//		Long time = parseDateTime(fields[timeIndex]);
//		Double value = Double.parseDouble(fields[dataIndex]);
//		return false;
//	}
	
	/*
	 * parses requests as JSON inputParser is used as the path to the nested json
	 * node i.e. inputParser could be: room1/sensors/temp/value
	 */
	private boolean parseJsonResponse(JsonNode root, Long currentTime) {
		// TODO check concat path against inputParser, get the goal and stop
		// TODO (linear time) form a list of nested path elements from the gui, and
		if (root==null) { return false; }
		String[] levels = inputParser.split("/");
		JsonNode node = root;  
		for (int i=1; i<levels.length; i++) {
			Logger.info(levels[i]);
			node = node.get(levels[i]);
			if (node==null) { return false; }
		}

		if (node.isValueNode()) { // it is a simple primative
			Logger.info("posting: "+node.getDoubleValue()+" "+Utils.currentTime());
			return stream.post(node.getDoubleValue(), Utils.currentTime());

		} else if (node.get("value") != null) { // it may be value:X
			double value = node.get("value").getDoubleValue();
			// should be Source timestamp

			if (node.get("time") != null) { // it may have  time:Y
				if(timeformat != null && !"".equalsIgnoreCase(timeformat) && !"unix".equalsIgnoreCase(timeformat)){
					currentTime = parseDateTime(node.get("time").getTextValue());
				} else {
					currentTime = node.get("time").getLongValue();
				}
			}
			Logger.info("posting: "+value);
			return stream.post(value, currentTime);
		}

		return false;
	}

	private Vfile getOrCreateStreamFile(String path) {
		if (source == null || (source != null && source.owner == null)) {
			Logger.error("[StreamParser] user does not exist.");
			return null;
		}
		if (path == null) {
			Logger.error("[Path] Null.");
			return null;
		}
		Vfile f = FileSystem.readFile(source.owner, path);
		if (f == null) {
			f = FileSystem.addFile(source.owner, path);
		} else if (f.getType() == Vfile.Filetype.DIR) {
			int i = 0;
			do {
				f = FileSystem.addFile( source.owner, path + "\\newstream" + Integer.toString(i) );
			} while( !FileSystem.fileExists( source.owner, path + "\\newstream" + Integer.toString(i++) ) );
		}
		if (f.getType() == Vfile.Filetype.FILE) {
			Stream stream = f.getLink();
			if (stream == null) {
				stream = Stream.create(new Stream(source.owner, this.source));
				f.setLink(stream);
				Logger.info("[StreamParser] Creating stream at: " + source.owner.getUserName()
						+ path);
			}
			return f;
		}
		Logger.error("[StreamParser] couldn't get or create a stream file in " + path);
		return null;
	}


	public static StreamParser create(StreamParser parser) {
		if (parser.source != null && parser.inputParser != null) {
			if (parser.stream == null) {
				if (parser.streamVfilePath == null) {
					parser.streamVfilePath = "/" + parser.source.label + "/newstream_" + (new Random(new Date().getTime()).nextInt(10000));
				}
				parser.stream = parser.getOrCreateStreamFile(parser.streamVfilePath).linkedStream;
			}
			parser.save();
			return parser;
		} else {
			Logger.warn("[StreamParser] Could not create parser for " + parser.source.label + ", source or input bad");
			if (parser.source == null) { Logger.warn("[StreamParser] source null"); }
			if (parser.inputParser == null) { Logger.warn("[StreamParser] input parser null"); }
		}
		Logger.warn("[StreamParser] Could not create parser for " + parser.source.label);
		return null;
	}
	
	public static void delete(Long id) {
		find.ref(id).delete();
	}
}
