
 {
	let preventiveList=new PreventiveList(
	        	document.getElementById("id_alert"),
	        	document.getElementById("id_your_preventives")),
	        	
	    personalMessage=new PersonalMessage(
			sessionStorage.getItem("username"),
			document.getElementById("id_username")),
			
		preventiveFinisher=new PreventiveFinisher(
				document.getElementById("id_free_preventives"));
					
	    pageOrchestrator=new PageOrchestrator();
	    
	    window.addEventListener("load", () => {
	    	if (sessionStorage.getItem("username") == null) {
	      		window.location.href = "login.html";
	    	} 
	    	else {
	      		pageOrchestrator.start();
	    	}
	  	}, false);
	  
	    function PersonalMessage(_username, messagecontainer) {
	    	this.username = _username;
	    	this.show = function() {
	      		messagecontainer.textContent = this.username;
	    	}
		}
		
		function PageOrchestrator(){
			this.start=function(){
				personalMessage.show();
				preventiveList.show();
				preventiveFinisher.show();
			};
		};
		
		function PreventiveList(_alert,_listcontainer){
		this.alert=_alert;
		this.listcontainer=_listcontainer;
		this.show=function(next){
			var self=this;
			makeCall("GET","GetPreventives",null,
			function(x){
				if (x.readyState == XMLHttpRequest.DONE) {
	            	var message = x.responseText;
	            	if (x.status == 200) {
	              		var preventivesToShow = JSON.parse(x.responseText);
	              		if (preventivesToShow.length == 0) {
	                		self.alert.textContent = "Non hai preventivi gestiti";
	                		return;
	              		}
	              		else{
							self.alert.textContent = "";
							self.update(preventivesToShow);
						} 
	            
	          		} 
                  	else {
	            		self.alert.textContent = message;
	          		}
	      		}
			})
		};
		this.update=function(arrayPreventives){
			var self=this;
			self.listcontainer.textContent="I tuoi preventivi:";
			arrayPreventives.forEach((preventive)=>{
				var prevmarkup=document.createElement("p");
				var printer1=document.createElement("div");
				printer1.textContent="Preventivo numero: "+preventive.ID;
				prevmarkup.appendChild(printer1);
				var printer2=document.createElement("div");
				printer2.textContent="Prodotto: "+preventive.productName;
				prevmarkup.appendChild(printer2)
				var printer3=document.createElement("div");;
				printer3.textContent="Cliente numero: "+preventive.IDClient;
				prevmarkup.appendChild(printer3);
				var printer4=document.createElement("div");
				printer4.textContent="Prezzo: "+preventive.price;
				prevmarkup.appendChild(printer4);
				self.listcontainer.appendChild(prevmarkup);
			});
			self.listcontainer.style.visibility="visible";
		};
	}
	
	function PreventiveFinisher(container){
		this.show=function(){
			makeCall("GET","GetUnhandled",null,
			function(x){
				if(x.readyState == XMLHttpRequest.DONE){
					if(x.status==200){
						var preventives=JSON.parse(x.responseText);
						if(preventives!=null){
							container.textContent="Preventivi non assegnati: ";
							preventives.forEach((preventive)=>{
								var printCode=document.createElement("p");
								printCode.textContent="Preventivo numero: "+preventive.ID+" ";
								var completeButton=document.createElement("input");
								completeButton.type="button";
								completeButton.value="Gestisci";
								completeButton.id=preventive.ID;
								completeButton.addEventListener("click",(e)=>Handle(e));
								printCode.appendChild(completeButton);
								container.appendChild(printCode);
						})	
					}else{
						container.textContent="Tutti i preventivi sono stati assegnati ";
					}
					}
					
					else{
						document.getElementById("id_alert").textContent=x.responseText;
					}
				}
			});
		}
	}
	
	function Handle(e){
		var form=document.createElement("form");
		var prev=e.target.closest("input").id;
		document.getElementById("preventive").value=prev;
		var selector=document.createElement("input");
		selector.value=prev;
		selector.name="prev";
		form.appendChild(selector);
		makeCall("POST","GetOptions",form,
		function(x){
			if(x.readyState == XMLHttpRequest.DONE){
				if(x.status==200){
					var elements=JSON.parse(x.responseText);
					var pricer=document.getElementById("id_price")
					pricer.textContent="Completa preventivo: "+prev;
					elements.forEach((element)=>{
						if(element!="" && element.type==undefined && element.productName==undefined){
							var text=document.createElement("div");
							text.textContent="Richiedente: "+element;
							document.getElementById("id_price").appendChild(text);
						}
						if(element.type!=undefined){
							var text=document.createElement("div");
							text.textContent=element.name+", "+element.type;
							document.getElementById("id_price").appendChild(text);
						}
						if(element.productName!=undefined){
							var text=document.createElement("div");
							text.textContent="Prodotto: "+element.productName;
							document.getElementById("id_price").appendChild(text);
						}
					})
					var inputbox=document.createElement("input");
					inputbox.type="text";
					inputbox.name="price";
					inputbox.id="price";
					pricer.appendChild(inputbox);
					var submitter=document.createElement("button");
					var msg=document.createElement("div");
					msg.id="sanity";
					submitter.type="button";
					submitter.textContent="Applica Prezzo";
					submitter.addEventListener("click",(e)=>AssignPrice(e));
					pricer.appendChild(submitter);
					pricer.appendChild(msg);
				}
				else{
					document.getElementById("id_alert").textContent=x.responseText;
				}
			}
		})
	}
	
	function AssignPrice(e){
		var form=document.createElement("form");
		var price=document.getElementById("price")
		if(parseInt(price.value)<=0 || price.value=="" || isNaN(parseInt(price.value))){
			document.getElementById("sanity").textContent="Inserire un prezzo valido";
		}
		else{
			form.appendChild(price);	
			var prv=document.createElement("input");
			prv.name="preventive";
			prv.value=document.getElementById("preventive").value;
			form.appendChild(prv);
			makeCall("POST","CompletePrev",form,
				function(x){
				if(x.readyState==XMLHttpRequest.DONE){
					if(x.status==200){
						preventiveList.show();
						preventiveFinisher.show();
						document.getElementById("id_price").textContent="";
					}
					else{
						document.getElementById("id_alert").textContent=x.responseText;
					}
				}
			})
		}
	}

}