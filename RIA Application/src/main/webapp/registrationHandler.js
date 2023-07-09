
 (function(){
	document.getElementById("registerbutton").addEventListener('click',(e)=>{
		var form=e.target.closest("form");
		var email=form.elements[2];
		var password=form.elements[4];
		var confirm=form.elements[5];
		if(!email.value.includes('@')||password.value!=confirm.value){
			document.getElementById("errormessage").textContent = "errore di formato";
		}
		else{
			makeCall("POST",'RegistrationHandler',form,
				function(x){
					if(x.readyState == XMLHttpRequest.DONE){
						var message = x.responseText;
			            switch (x.status) {
			              case 200:
			                window.location.href = "login.html";	
			                break;
			              case 400: 
			                document.getElementById("errormessage").textContent = message;
			                break;
			              case 401: 
			                  document.getElementById("errormessage").textContent = message;
			                  break;
			              case 500: 
			            	document.getElementById("errormessage").textContent = message;
			                break;
					}
				}
			});
		}
		
	});
	
})();