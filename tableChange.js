function allButtonClick()
{
  if(document.getElementById("AllCB").checked == true)
  {
    showRequired("info");
    showRequired("passed");
    showRequired("failed");    
    showRequired("message");
    showRequired("warning");
    hideRequired("browserLogs");
    document.getElementById("InfoCB").checked = false;
    document.getElementById("PassedCB").checked = false;
    document.getElementById("FailedCB").checked = false;
    document.getElementById("WarningCB").checked = false;
    document.getElementById("MessageCB").checked = false;
    document.getElementById("BrowserLogsCB").checked = false;
  }
  else
  {
    hideRequired("info");
    hideRequired("passed");
    hideRequired("failed");    
    hideRequired("message");
    hideRequired("warning");
    buttonClick();
  }
}

function buttonClick(){
  var selectedValue;
  document.getElementById("AllCB").checked = false;

 if(document.getElementById("InfoCB").checked == true)
 {
  showRequired("info");  
 }
 else{
hideRequired("info");

 }
 
 if(document.getElementById("PassedCB").checked == true)
 {  
  showRequired("passed");  
 }
 else{
hideRequired("passed");
 }

 if(document.getElementById("FailedCB").checked == true)
 {
  showRequired("failed");
 }
 else {
hideRequired("failed");
 }
 
 if(document.getElementById("WarningCB").checked == true)
 {
  showRequired("warning");
 }
 else {
hideRequired("warning");
 }
 
 if(document.getElementById("MessageCB").checked == true)
 {
  showRequired("message");
 }
 else{ 
  hideRequired("message");
 }

} 

function browserLogButtonClick()
{
  if(document.getElementById("BrowserLogsCB").checked == true)
  {
    showRequired("browserLogs");
  }
  else
  { 
    hideRequired("browserLogs");  
  }
}

function showRequired(selectedValue)
{
  table = document.getElementById("each_test_report");
  tr = table.getElementsByTagName("tr");
  // Loop through all table rows, and hide those who don't match the search query
  for (i = 1; i < tr.length; i++) 
  {
    if(tr[i].id == selectedValue)
        tr[i].style.display = "";
  }
}

function hideRequired(selectedValue)
{
  table = document.getElementById("each_test_report");
  tr = table.getElementsByTagName("tr");
  // Loop through all table rows, and hide those who don't match the search query
  for (i = 1; i < tr.length; i++) 
  {
    if(tr[i].id == selectedValue)
        tr[i].style.display = "none";
  }
}

function removeAll()
{
  table = document.getElementById("test_status");
  tr = table.getElementsByTagName("tr");
  // Loop through all table rows, and hide those who don't match the search query
  for (i = 1; i < tr.length; i++) 
  {
        tr[i].style.display = "none";
  }
}

function xmlTestNameClick()
{
	removeAll();

  var divElement = document.getElementById("xmlTestNames");
  var inputs = divElement.getElementsByTagName("input");
  for(var i = 0; i < inputs.length; i++) 
  {
    if(inputs[i].type == "checkbox") 
    {
        if(inputs[i].checked == true)
          showTest(inputs[i].id);
    }
  }
}

function showTest(selectedValue)
{
	table = document.getElementById("test_status");
	tr = table.getElementsByTagName("tr");
  	// Loop through all table rows, and hide those who don't match the search query
  	for (i = 1; i < tr.length; i++) 
  	{
  		if(selectedValue == "All")
  			tr[i].style.display = "";
  		else
  		{
  			td = tr[i].getElementsByTagName("td");
    		if(td[1].innerText == selectedValue)
       			tr[i].style.display = "";
       }
  	}
}

function getElementByXpath(path) 
{
  return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
}