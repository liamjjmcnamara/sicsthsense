package controllers;

//import models.Project;
//import models.Task;
//import models.User;
import java.util.HashMap;

import play.*;

import play.core.Router.Routes;
import play.libs.F.*;
import play.libs.*;
import play.mvc.*;
import play.data.*;

import models.*;
import views.html.*;

@Security.Authenticated(Secured.class)
public class CtrlUser extends Controller {
  
  static private Form<User> userForm = form(User.class);
  
  static public User getUser() {
    return User.get(Long.parseLong(session().get("id")));
  }
  
  public static Result get() {
    return ok(ViewUser.render(null));
  }
  
  public static Result edit() {
    return ok(ViewUser.render(userForm));
  }
  
  public static Result submit() {
    Form<User> theForm = userForm.bindFromRequest();
    if(theForm.hasErrors()) {
      return badRequest("Bad request");
    } else {
      User current = getUser();
      User submitted = theForm.get();
      submitted.id = current.id;
      submitted.email = current.email;
      submitted.update();
      return redirect(routes.CtrlUser.get());
    }
  }
    
}