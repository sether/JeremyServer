<!DOCTYPE HTML>
<!--
	Solarize by TEMPLATED
	templated.co @templatedco
	Released for free under the Creative Commons Attribution 3.0 license (templated.co/license)
-->
<html>
	<head>
		<title>Jeremy CSV Converter - Help</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<!--[if lte IE 8]><script src="css/ie/html5shiv.js"></script><![endif]-->
		<script src="js/jquery.min.js"></script>
		<script src="js/jquery.dropotron.min.js"></script>
		<script src="js/skel.min.js"></script>
		<script src="js/skel-layers.min.js"></script>
		<script src="js/init.js"></script>
		<noscript>
			<link rel="stylesheet" href="css/skel.css" />
			<link rel="stylesheet" href="css/style.css" />
		</noscript>
		<!--[if lte IE 8]><link rel="stylesheet" href="css/ie/v8.css" /><![endif]-->
	</head>
	<body class ="homepage">
		<!-- Header Wrapper -->
	<div class="wrapper style1">

		<!-- Header -->
		<div id="header">
			<div class="container">

				<!-- Logo -->
				<h1>
					<a href="#" id="logo">Solarize</a>
				</h1>

				<%@ include file="nav.jsp" %>

			</div>
		</div>
		
		<!-- Main -->
			<div id="main" class="wrapper style4">

				<!-- Content -->
				<div id="content" class="container">
					<section>
						<header class="major">
							<h2>Contact</h2>
							<span class="byline">Fill in the data to contact us!</span>
						</header>
						<form>
						<p>
						Name:<br>
						<input type="text" name="name">
						</p>
						<p>
						Email<br>
						<input type="text" name="email">
						</p>
						<p>
						Message<br>
						<textarea name="message" rows="6" cols="30"></textarea>
						</p>
						<p>
						<input type="submit" value="Submit">
						</p>
						</form>
					</section>
				</div>
			</div>
			<%@ include file="footer.html" %>
	</body>
</html>