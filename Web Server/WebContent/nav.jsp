<nav id="nav">
	<ul>
		<li class="active"><a href="index.jsp">Home</a>
		<li><a href="converter">Converter</a>
		<li><a href="contactUs.jsp">Contact Us</a>
		<li><a href="documentation.html">Documentation</a>
		
		<%
		String status = (String) session.getAttribute("status");
		if(status != null && status.equals("Registered")){
			out.print("<li><a href='logout'>Logout</a>");
		} else {
			out.print("<li><a href='login'>Login</a>");
		}
		%>
	</ul>
</nav>