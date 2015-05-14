<!DOCTYPE HTML>
<html>
	<head>
		<title>Contact Us</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<!--[if lte IE 8]><script src="css/ie/html5shiv.js"></script><![endif]-->
		<script src="js/jquery.min.js"></script>
		<script src="js/jquery.dropotron.min.js"></script>
		<script src="js/skel.min.js"></script>
		<script src="js/skel-layers.min.js"></script>
		<script src="js/init.js"></script>
		</head>
	<body>

		<!-- Header Wrapper -->
			<div class="wrapper style1">
			
			<!-- Header -->
				<div id="header">
					<div class="container">
						<%@ include file="nav.jsp" %>
					</div>
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
			<script src="/WebContent/footer.html"></script>
	</body>
</html>