
 {
	let productsList=new ProductsList(
				document.getElementById("id_products")),
				
		preventiveList=new PreventiveList(
	        	document.getElementById("id_alert"),
	        	document.getElementById("id_preventivelist")),
	        	 
		personalMessage=new PersonalMessage(
			sessionStorage.getItem("username"),
			document.getElementById("id_username")),
					
	    pageOrchestrator=new PageOrchestrator();

	window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "login.html";
	    } else {
	      document.getElementById("product_button").addEventListener("click",(e)=>ShowOptions(e));
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
			productsList.set();
		};
		this.reset=function(){
			document.getElementById("id_preventivelist").textContent="";
			document.getElementById("id_products").textContent="";
			document.getElementById("id_options").textContent="";
			document.getElementById("id_alert").textContent="";
			document.getElementById("id_details").textContent="";
		}
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
	                		self.alert.textContent = "Non hai preventivi";
	                		return;
	              		}
	              		self.update(preventivesToShow);	        
	          		} 
                  	else {
	            		self.alert.textContent = message;
	          		}
	      		}
			})
		};
		this.update=function(arrayPreventives){
			var self=this;
			arrayPreventives.forEach((preventive)=>{
				var details=document.createElement("input");
				details.type="button";
				details.value="Dettagli";
				details.addEventListener("click",(e)=>ShowDetails(e));
				var prevmarkup=document.createElement("p");
				prevmarkup.textContent=preventive.ID+" "+preventive.productName;
				details.id=preventive.ID;
				prevmarkup.appendChild(details);
				self.listcontainer.appendChild(prevmarkup);
			});
			self.listcontainer.style.visibility="visible";
		};
	}
	
	function ProductsList(productform){
		this.productlist=productform;
		this.set=function(){
			var self=this;
			makeCall("GET","GetProducts",null,
			function(x){
				if (x.readyState == XMLHttpRequest.DONE){
					if(x.status == 200){
						var products=JSON.parse(x.responseText);
						self.show(products);
					}
					else{
						document.getElementById("id_alert").textContent=x.responseText;
					}
				}				
			});			
		}
		this.show=function(productsArray){
			var self=this;
			productsArray.forEach((product)=>{
				var choice=document.createElement("option");
				choice.textContent=product.name
				choice.value=product.code;
				self.productlist.appendChild(choice);
			})
		}
	}
	
	function ShowOptions(e){
		document.getElementById("id_options").textContent="";
		var prod=document.createElement("input");
		prod.id="productid";
		prod.type="hidden";
		prod.value=document.getElementById("id_products").value;
		document.getElementById("id_products").appendChild(prod);
		var form=e.target.closest("form");
		if(form.checkValidity()){
			makeCall("POST","GetOptions",form,
			function(x){
				if(x.readyState == XMLHttpRequest.DONE){
					if(x.status == 200){
						var options=JSON.parse(x.responseText);
						var Form=document.createElement("form");
						Form.id="checkbox_form";
						options.forEach((option)=>{
							var opt=document.createElement("input");
							opt.type="checkbox";
							opt.value=option.code;
							Form.appendChild(opt);
							var label=document.createElement("label");
							label.textContent=option.name+", "+option.type;
							Form.appendChild(label);
							Form.appendChild(document.createElement("br"));
						})	
						var optionButton=document.createElement("input");
						optionButton.type="button";
						optionButton.value="Crea preventivo";
						optionButton.addEventListener("click",(e)=>CreatePreventive(e));
						Form.appendChild(optionButton);
						document.getElementById("id_options").appendChild(Form);
					}
					else{
						document.getElementById("id_alert").textContent=x.responseText;
					}
				}
			})	
		}
		
	}
	
	function ShowDetails(e){
		document.getElementById("id_details").textContent="";
		var form=document.createElement("form");
		var pice=document.createElement("input");
		pice.name="preventiveid";
		var previd=e.target.closest("input").id;
		pice.value=previd;
		form.appendChild(pice);
		
		makeCall("POST","ShowDetails",form,
		function(x){
			if(x.readyState == XMLHttpRequest.DONE){
				if(x.status==200){
					var details=JSON.parse(x.responseText);
					var title=document.createElement("div");
					title.textContent="Dettagli preventivo "+previd+":";
					document.getElementById("id_details").appendChild(title);
					details.forEach((detail)=>{
						if(detail.length!=undefined){
							for(var i=0;i<detail.length;i++){
								var text=document.createElement("div");
								text.textContent=detail[i].name+", "+detail[i].type;
								document.getElementById("id_details").appendChild(text);
							}
						}
						if(detail.price!=undefined){
							var text=document.createElement("div");
							if(detail.price!=0){
								text.textContent="Prezzo: "+detail.price;
							}
							else{
								text.textContent="Prezzo: non ancora definito, richiesta presa in carico";
							}
							document.getElementById("id_details").appendChild(text);
						}
						
						
					})
				}
				else{
					document.getElementById("id_alert").textContent=x.responseText;
				}
			}
		})
	}
	
	function CreatePreventive(e){
		var checkbox=document.getElementById("checkbox_form");
		var form=document.createElement("form");
		var id=0;
		var prod=document.createElement("input");
		prod.name="prod";
		prod.value=document.getElementById("productid").value;
		for(var i=0;i<checkbox.elements.length;i++){
			if(checkbox.elements[i].checked==true){
				checkbox.elements[i].name=id;
				form.appendChild(checkbox.elements[i])
				id+=1;
			}
		}
		if(form.elements.length>0){
			form.appendChild(prod);
			makeCall("POST","CreatePrev",form,
			function(x){
				if(x.readyState == XMLHttpRequest.DONE){
					if(x.status==200){
						pageOrchestrator.reset();
						pageOrchestrator.start();
					}
					else{
						document.getElementById("id_alert").textContent=x.responseText;						
					}
				}
			})
		}
		else{
			document.getElementById("id_alert").textContent="Scegliere almeno un'opzione";
		}
	}
}