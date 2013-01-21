// 	function followButton(event){
// 		var $this_button=$(this);
// 		var my_endpoint_id=$this_button.attr('parent_id');
// 	  jsRoutes.controllers.CtrlEndPoint.follow(my_endpoint_id).ajax({
// 	  	success: function() {
// 	    	$this_button.toggleClass("icon-star unfollow_endpoint icon-star-empty follow_endpoint");    	
// 	    	$('div.container-errormsg').html('<strong>Cool follow!</strong>');
// 	    	event.stopImmediatePropagation();
// 	    	event.stopPropagation();
// 	    	event.preventDefault();
// 	    	//$this_button.on("click", unfollowButton);
// 	    	return false;
// 	    },
// 	    error: function() {
// 	    	$('div.container-errormsg').html('<strong>Error follow!</strong>');
// 	    }
// 	  });
// 	};
	
// 	function unfollowButton(event){
// 		var $this_button=$(this);
// 		var my_endpoint_id=$this_button.attr('parent_id');
// 	  jsRoutes.controllers.CtrlEndPoint.unfollow(my_endpoint_id).ajax({
// 	    success: function() {
// 	    	$this_button.toggleClass("icon-star-empty follow_endpoint icon-star unfollow_endpoint");
// 	    	$('div.container-errormsg').html('<strong>Cool unfollow!</strong>');
// 	    	event.stopImmediatePropagation();
// 	    	event.stopPropagation();	    	
// 	    	event.preventDefault();
// 	    	//$this_button.on("click", followButton);
// 	    	return false;
// 	    },
// 	    error: function() {
// 	    	$('div.container-errormsg').html('<strong>Error unfollow!</strong>');
// 	    }
// 	  });
// 	};
	
	function toggleFollowEndpointButton(event){
		var $this_button=$(this);
		var my_endpoint_id=$this_button.attr('parent_id');
	  jsRoutes.controllers.CtrlEndPoint.toggleFollow(my_endpoint_id).ajax({
	    success: function(msg) {
	    	$this_button.toggleClass("icon-star-empty follow_endpoint icon-star unfollow_endpoint");
	    	$('div.container-errormsg').html('<strong>Cool!</strong>');	    	
	    	//event.stopImmediatePropagation();
	    	//event.stopPropagation();	    	
	    },
	    error: function(emsg) {
	    	$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
	    	jsRoutes.controllers.CtrlEndPoint.isFollowing(my_endpoint_id).ajax({
	  	    success: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
	  	    	if(fmsg=="1") {
	  	    		$this_button.removeClass("icon-star-empty follow_endpoint");
	  	    		$this_button.addClass("icon-star unfollow_endpoint");
	  	    	} else if(fmsg=="0") {
	  	    		$this_button.removeClass("icon-star unfollow_endpoint");
	  	    		$this_button.addClass("icon-star-empty follow_endpoint");
	  	    	} 
	  	    },
	  	    error: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    }
	  	  });
	    }
	  });
	  return false;
	};
	$('.unfollow_endpoint').on("click", toggleFollowEndpointButton);
	$('.follow_endpoint').on("click", toggleFollowEndpointButton);

	function toggleFollowResourceButton(event){
		var $this_button=$(this);
		var my_resource_id=$this_button.attr('parent_id');
	  jsRoutes.controllers.CtrlResource.toggleFollow(my_resource_id).ajax({
	    success: function(msg) {
	    	$this_button.toggleClass("icon-star-empty follow_resource icon-star unfollow_resource");
	    	$('div.container-errormsg').html('<strong>Cool!</strong>');	    	
	    	//event.stopImmediatePropagation();
	    	//event.stopPropagation();	    	
	    },
	    error: function(emsg) {
	    	$('div.container-errormsg').html('<strong>Error unfollow!</strong>'+emsg);
	    	jsRoutes.controllers.CtrlResource.isFollowing(my_endpoint_id).ajax({
	  	    success: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Reparing Error!</strong>'+fmsg);
	  	    	if(fmsg=="1") {
	  	    		$this_button.removeClass("icon-star-empty follow_resource");
	  	    		$this_button.addClass("icon-star unfollow_resource");
	  	    	} else if(fmsg=="0") {
	  	    		$this_button.removeClass("icon-star unfollow_resource");
	  	    		$this_button.addClass("icon-star-empty follow_resource");
	  	    	} 
	  	    },
	  	    error: function(fmsg) {
	  	    	$('div.container-errormsg').html('<strong>Error!</strong>'+fmsg);
	  	    }
	  	  });
	    }
	  });
	  return false;
	};
	$('.unfollow_resource').on("click", toggleFollowResourceButton);
	$('.follow_resource').on("click", toggleFollowResourceButton);

	function hideResourceList(event){
		$(this).toggleClass("icon-chevron-down icon-chevron-up");
		//$(this).children().find('.icon-chevron-up').toggleClass("icon-chevron-down").toggleClass("icon-chevron-up");
		$(this).parent().find('.resource_list').toggle();
		return false;
	};

	$('.hide_resources').on("click", hideResourceList);
	