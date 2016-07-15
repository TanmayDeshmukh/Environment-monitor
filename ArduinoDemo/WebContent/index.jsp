<%@page import="com.SerialTest"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
   <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
</head>
<body>
<jsp:useBean id="sensor" class="com.SerialTest" scope="application"></jsp:useBean>
<%
if(!SerialTest.all_inputs_updated)
{
sensor.start();
//sensor.trigger();
//sensor.close();
Thread.sleep(5000);
}
%>
 <script type="text/javascript">
      google.charts.load('current', {'packages':['corechart']});
      google.charts.setOnLoadCallback(drawChart2);
      google.charts.setOnLoadCallback(drawChart);

      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          <%=SerialTest.plotdata1%>
        ]);
        
        var options = {
          title: 'Sensor history',
          curveType: 'function',
          legend: { position: 'bottom' }
        };
        var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));
        chart.draw(data, options);
      }
        
        
      function drawChart2(){
    	  var data = google.visualization.arrayToDataTable([
    	  <%=SerialTest.plotdata2%>
    	                                                  ]);
    	                                                  
    	  var options = {
    	  title: 'Sensor History',
    	  curveType: 'function',
    	  legend: { position: 'bottom' }
    	  };
    	  var chart = new google.visualization.LineChart(document.getElementById('curve_chart2'));
    		chart.draw(data, options);
      }
    </script>
    
    
    
    
    
    
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#">Environment Monitor</a>
    </div>
    <ul class="nav navbar-nav">
      <li class="active"><a href="#">Home</a></li>
    </ul>
  </div>
</nav>

<table class="table table-responsive table-striped">
<tr><td>Parameter</td><td>Value</td><td>Unit</td></tr>
<tr><td> </td><td> </td><td> </td></tr>
	<tr><td>Temperature</td><td><%=SerialTest.temp[SerialTest.buffers-1]%></td><td>C</td></tr>
	<tr><td>Humidity</td><td><% out.println(SerialTest.humi[SerialTest.buffers-1]);%></td><td>percent</td></tr>
	<tr><td>Pressure</td><td><% out.println(SerialTest.pres[SerialTest.buffers-1]);%></td><td>Pa</td></tr>
	<tr><td>Light</td><td><% out.println(SerialTest.light[SerialTest.buffers-1]);%></td><td>vol</td></tr>
	<tr><td>Dust</td><td><% out.println(SerialTest.dust[SerialTest.buffers-1]);%></td><td>pcs/0.01cf</td></tr>
</table>
<table class="column">
<tr>
 <td><div id="curve_chart" style="width: 800px; height: 400px"></div></td>
 <td><div id="curve_chart2" style="width: 800px; height: 400px"></div></td>
</tr>
</table>

</body>
</html>