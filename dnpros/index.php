<?php
	if ($_POST["submit"]) {
		$position = $_POST['position'];
		$original = $_POST['original'];
		$mutated = $_POST['mutated'];
		$sequence = $_POST['sequence'];
		$order = 'ACDEFGHIKLMNPQRSTVWY';
											
		if (!$_POST['position'] || !$_POST['original'] || !$_POST['mutated']) {
			$err = 'Please fill out all the mutation information.';
		}
		else if (strpos($order,$original)===false) {
			$err = '<strong>'. $original. '</strong> is not a regular amino acid. Make sure it is a capital letter.';
		}
		else if (strpos($order,$mutated)===false) {
			$err = '<strong>' .$mutated. '</strong> is not a regular amino acid. Make sure it is a capital letter.';
		}
		else {
			$temp = substr($sequence,$position-1,1);
			if ($temp!==$original) {
				$err = 'The number <strong>'.$position.'</strong> element is '. $temp.' not <strong>'. $original.'</strong>.';
			}
		}
		if (strlen($sequence) < 7) { 
			$errSequence = 'Your Sequence is less than <strong>7</strong> characters';
		}
											
		if (!$err&&!$errSequence) {
			$waiting = $_SERVER["REQUEST_TIME"]."_".$_SERVER["REMOTE_ADDR"]."_".mt_rand();
			$count = $position-4;
			$ssss = substr($sequence, max($position-4,0), 7);
			while($count<0) {
				$ssss = "~".$ssss;
				$count++;
			}
			$agenda = "/var/www/html/dnpros/bin/Single/".$ssss;
			$input = $agenda. "/".$original.$position.$mutated.".txt";
			$catalog = "bin/job_queue/".$waiting;


			$bool = file_exists($input);
			if(!$bool) {
				system('mkdir '. $agenda);
				system('chmod 777 '. $agenda);
				system('touch '.$catalog.'_is.ready');
				$files = glob('bin/job_queue/*.running');
				$cou = glob('bin/job_queue/*.ready');
				while(count($files)>10||$cou[0]!=$catalog.'_is.ready'){
					sleep(2);
					$files = glob('bin/job_queue/*.running');
					$cou = glob('bin/job_queue/*.ready');
				}
				system('mv '.$catalog.'_is.ready '.$catalog.'_is.running');
				system('java -cp bin SingleMutationPrediction '. $position. ' '. $original. ' '. $mutated. ' '. $sequence. ' '. $input, $output);
			}

			$file = fopen($input, "r") or die("file is not available");
			$text = fgets($file);
			$value = number_format(floatval($text)*100,2);
			if($value>=50) {
				$result = "INCREASE";
				$color = "#46BFBD";
			} else {
				$result = "DECREASE";
				$color = "#F7464A";
			}
			fclose($file);
			if(file_exists($catalog."_is.running")) {
				system('mv '.$catalog.'_is.running '.$catalog.'_is.complete');
				}
			
			$files = glob('bin/job_queue/*.writing');
			while(count($files)>0) {
				sleep(2);
				$files = glob('bin/job_queue/*.writing');
			}
			if(file_exists($catalog."_is.complete")) {
				system('mv '.$catalog.'_is.complete '.$catalog.'_is.writing');
			} else {
				system('touch '.$catalog.'_is.writing');
			}
			$file = fopen('bin/job_queue/history.txt', "a");
			fwrite($file, ">".$waiting."\r\n".$sequence."\r\n".$original.$position.$mutated."\t".$text."\r\n");
			fclose($file);
			system('rm '.$catalog.'_is.writing');
			
		}
	}
	if($_POST["submit1"]) {
		
		$oriSeq = $_POST['oriSeq'];
		$mutSeq = $_POST['mutSeq'];
		if (strlen($oriSeq) < 7 || strlen($mutSeq) < 7) { 
			$errSeq = 'Your Sequence is less than <strong>7</strong> characters';
		}
		else if(strlen($oriSeq)!==strlen($mutSeq)) {
			$errSeq = 'Your Sequences are not in the same length.';
		} 
		if(!$errSeq) {
			$rand = mt_rand();
			$waiting = $_SERVER["REQUEST_TIME"]."_".$_SERVER["REMOTE_ADDR"]."_".$rand;
			$t = substr($oriSeq, 0, 10). substr($mutSeq, 0, 10). $rand;
			$catalog = "/var/www/html/dnpros/bin/Sequence/".$t;
			system('mkdir '. $catalog);
			system('chmod 777 '. $catalog);
			system('touch bin/job_queue/'.$waiting.'_is.ready');
			$threads = glob('bin/job_queue/*.running');
			$col = glob('bin/job_queue/*.ready');
				while(count($threads)>10||$col[0]!='bin/job_queue/'.$waiting.'_is.ready'){
					sleep(2);
					$threads = glob('bin/job_queue/*.running');
					$col = glob('bin/job_queue/*.ready');
				}
			system('mv bin/job_queue/'.$waiting.'_is.ready '. 'bin/job_queue/'.$waiting. '_is.running');
			system('java -cp bin SequenceComparePrediction '. $oriSeq. ' '. $mutSeq. ' '.$catalog, $output);
			$content = file_get_contents($catalog.'/mutation.txt') or die("file is not available");
			$array = explode("\n",$content);
			$num = count($array);
			$test = array();
			$positive = 0;
			for($i=0; $i<count($array); $i++) {
				$temp = explode("\t", $array[$i]);
				$value = number_format(floatval($temp[1])*100,2);
				if($value>=50) {
					$positive++;
				}
				$test[$temp[0]] = $value;
			}
			$per = number_format($positive/$num*100,1);
			system('mv bin/job_queue/'.$waiting.'_is.running '. 'bin/job_queue/'.$waiting. '_is.complete');
			$threads = glob('bin/job_queue/*.writing');
			while(count($threads)>0) {
				sleep(2);
				$threads = glob('bin/job_queue/*.writing');
			}
			system('mv bin/job_queue/'.$waiting.'_is.complete '. 'bin/job_queue/'.$waiting. '_is.writing');
			$file = fopen('bin/job_queue/history.txt', "a");
			fwrite($file, ">".$waiting."\r\n".$oriSeq."\r\n".$mutSeq."\r\n".$content."\r\n\r\n");
			fclose($file);
			system('rm bin/job_queue/'.$waiting.'_is.writing');
		}
	}
	
?>



<!DOCTYPE html>
<html>


	<head>
		<title>DBN</title>

		<link href="format/css/bootstrap.min.css" rel="stylesheet" media="screen">
		<link rel="stylesheet" type="text/css" href="try.css">

		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
        	<script src="format/js/bootstrap.min.js" type="text/javascript"></script>
		<script src="format/js/Chartmaster/Chart.js" type="text/javascript"></script>
	</head>

	<body>
		<nav class="navbar navbar-custom2" role="navigation">
			<div class="container-fluid">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
						<span class="sr-only">Toggle navigation</span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="index.php">Home</a>
				</div>
				<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
					<ul class="nav navbar-nav">
						<li><a href="index.php">DBN<span class="sr-only">(current)</span></a></li><li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Method <span class="caret"></span></a>
							<ul class="dropdown-menu" role="menu">
								<li class="offset-right dropdown">
									<a class="dropdown-toggle" style="color:#4D5360">Single Mutation               </a>
									<ul class="dropdown-menu">
										<li>
											<div class="col-lg-12">
											<p style="text-align: justify; position:relative; top:5px;">&nbsp;&nbsp;&nbsp;&nbsp;This method is focus on predicting protein energy change of single site mutation. The single mutation information is required in this case. Such as position, original amino acid, mutated amino acid and protein sequence.</p>
											</div>
										</li>
									</ul>
								</li>
								<li class="offset-right dropdown">
									<a class="dropdown-toggle" style="color:#4D5360">Compare Sequence               </a>
									<ul class="dropdown-menu">
										<li>
											<div class="col-lg-12">
											<p style="text-align: justify; position:relative; top:5px">&nbsp;&nbsp;&nbsp;&nbsp;Given two sequences with the same length, this method will find the differences or mutations first, then predict them through DBN model. Original sequence and Mutated Sequence should be both provided for this case.</p>
											</div>
										</li>
									</ul>
								</li>
								<li class="divider"></li>
								<li class="disabled"><a >Text File Prediction</a></li>
							</ul>
						</li>
						<li><a href="about.htm">About</a></li>
						
					</ul>
					
					
				</div>
			</div>
		</nav>
		<div class="container">
			
<!-----------------------------Panel--------------------------------------->
			<div class="row">
				
				<div class="col-lg-6">
					<div class="panel panel-primary" style="border-color:#7dacf2">
						<div class="panel-heading" style="background-color:#629bf0; border-color:#7dacf2">
							<h3 class="panel-title">Introduction</h3>
						</div>
						<div class="panel-body">
							<div class="col-lg-12">
<!-----------------------------Tabs----------------------------------------->
							<p style="text-align:justify; color:#4D5360">&nbsp;&nbsp;&nbsp;&nbsp;Deep Belief Network is branch of Deep Learning stacking several layers of Restricted Boltzmann Machine. It produce good result on both supervised and unsupervised learning.This is a DBN classification tool in bioinformatic focus on predicting protein stability change caused by single-site amino acid mutation. Three methods can be used in this tool while you should provide at least sequence information and mutation information.</p>

							</div>
						</div>
					</div>
					<div class="panel panel-primary" style="border-color:#7dacf2">
						<div class="panel-heading" style="background-color:#629bf0; border-color:#7dacf2">
							<h3 class="panel-title">Deep Belief Network</h3>
						</div>
						<div class="panel-body">
<!-----------------------------Tabs----------------------------------------->
							
							<ul class="nav nav-tabs" id="tabs">
								<li class="active"><a href="#home" data-toggle="tab" aria-expanded="true" >Single Mutation</a></li>
								<li class=""><a href="#profile1" data-toggle="tab" aria-expanded="false" >Compare Sequence</a></li>
								<li class="disabled"><a>Text Files</a></li>
							</ul>
							<script>
								$(function() { 
									$('a[data-toggle="tab"]').on('shown.bs.tab', function () {
									//save the latest tab; use cookies if you like 'em better:
										localStorage.setItem('lastTab', $(this).attr('href'));
									});

									//go to the latest tab, if it exists:
									var lastTab = localStorage.getItem('lastTab');
									if (lastTab) {
										$('a[href=' + lastTab + ']').tab('show');
									}
									else {
									 //Set the first tab if cookie do not exist
										$('a[data-toggle="tab"]:first').tab('show');
									}
								});
							</script>
							<div id="myTabContent" class="tab-content">

<!-----------------------------Home------------------------------------------>
								<div class="tab-pane fade active in" id="home">
									
									<br>
									<p style="text-align:justify; color:#4D5360">&nbsp;&nbsp;&nbsp;&nbsp;Single mutation information is required in this case. Such as position, original amino acid, mutated amino acid and original protein sequence.</p>
									<br>
											
									<form class="form-horizontal" role="form" method="post" action="index.php">
												
										<div class="form-group">
											<label class="col-lg-2 control-label" style="color:#4D5360">Mutation</label>
											<div class="col-lg-10" style="width: 27%">
												<input type="text" class="form-control" id="position" name="position" placeholder="Position">
											</div>
											<div class="col-lg-10" style="width: 28%">
												<input type="text" class="form-control" id="original" name="original" placeholder="Original">
											</div>
											<div class="col-lg-10" style="width: 28%">
												<input type="text" class="form-control" id="mutated" name="mutated" placeholder="Substitude">
											</div>
										<?php echo "<div class='col-lg-10 col-lg-offset-2'><p class='text-danger'>$err</p></div>";?>
										</div>

										<div class="form-group">
											<label class="col-lg-2" style="color:#4D5360">Sequence</label>
											<div class="col-lg-10">
												<textarea class="form-control" rows="3" id="sequence" name="sequence"></textarea>
												<?php echo "<p class='text-danger'>$errSequence</p>";?>
												<span class="help-block" style="color:#4D5360">A sequence with over 
													<span style = "color: red; font-weight: bold"> 7 </span>
												amino-acids is preferred.</span>
											</div>
										</div>

										<div class="form-group">
											<div class="col-lg-10 col-lg-offset-8">
												<button type="reset" id="cancel" name="cancel" class="btn btn-default" style="width: 18%; color:#4D5360">Cancel</button>
												<input type="submit" id="submit" name="submit" class="btn btn-primary" value="Submit" style="width: 17%; background-color:#629bf0; border-color:#7dacf2"><!--data-toggle="modal" href="#mymodal"-->
											</div>

											<div class="modal" id="mymodal">
												<div class="modal-dialog">
													<div class="modal-content">
														<div class="modal-header">
															<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
															<h4 class="modal-title">Calculating</h4>
														</div>
														<div class="modal-body">
															<p>The result will be shown in few seconds.</p>
														</div>
														<div class="modal-footer">
															<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
															<button type="button" class="btn btn-primary">OK</button>
														</div>
													</div>
												</div>
											</div>
													
										</div>

									</form>
									
								</div>

<!-----------------------------Profile1--------------------------------------->
								<div class="tab-pane fade" id="profile1">
									<br>
										<p style="text-align:justify; color:#4D5360">&nbsp;&nbsp;&nbsp;&nbsp;Mutated sequence will be compared to original sequence in this case. A set of mutations will be listed showing the change of protein stability.</p>
									<br>
								<form class="form-horizontal" role="form" method="post" action="index.php">

									<div class="form-group">
										<label class="col-lg-2 control-label" style="color:#4D5360">Original Sequence</label>
										<div class="col-lg-10">
											<textarea class="form-control" rows="3" id="oriSeq" name="oriSeq"></textarea>
											<span class="help-block" style="color:#4D5360">A sequence with over 
												<span style = "color: red; font-weight: bold"> 7 </span>
											amino-acids is preferred.</span>
										</div>
										<label class="col-lg-2 control-label" style="color:#4D5360">Mutated Sequence</label>
										<div class="col-lg-10">
											<textarea class="form-control" rows="3" id="mutSeq" name="mutSeq"></textarea>
											<span class="help-block" style="color:#4D5360">
												<?php if(!$_POST['submit1'] || !$errSeq) { 
													echo "Length of Sequences must be the equal. Miss values in both sequences could be labeled as \"<span style = 'color: red; font-weight: bold'> ~ </span>\"";
												} else {
													echo "<p class='text-danger'>$errSeq</p>";
												}
												?>
											</span>
										</div>
									</div>
									<div class="form-group">
										<div class="col-lg-10 col-lg-offset-8">
											<button type="reset" class="btn btn-default" style="width: 18%; color:#4D5360">Cancel</button>
											<input type="submit" id="submit1" name="submit1" class="btn btn-primary" style="width: 17%; background-color:#629bf0; border-color:#7dacf2"value="Submit"></input>
										</div>
									</div>
								</form>
								</div>
<!-----------------------------Profile2--------------------------------------->
								<div class="tab-pane fade" id="profile2">
									<br>
									<p>&nbsp;&nbsp;&nbsp;&nbsp;A text file of mutation information could be uploaded in this case. The mutation information should be input in specified format.</p>
									<br>
									<div class="form-group">
										<label class="col-lg-2 control-label">Upload</label>
										<div class="col-lg-10">
											<input id="file-0" class="file" type="file" multiple=true>
										</div>
									<br>
									<br>
									</div>
									<div class="form-group">
										<label class="col-lg-2 control-label">Email</label>
										<div class="col-lg-10" >
											<input type="text" class="form-control" id="email" name="email" placeholder="email@domain.com" value="<?php echo htmlspecialchars($_POST['email']); ?>">
										</div>
									<?php echo "<div class='col-lg-10 col-lg-offset-2'><p class='text-danger'>$errEmail</p></div>";?>
									<br>
									</div>
									<br>
									
									<div class="form-group">
										<div class="col-lg-10 col-lg-offset-8">
											<button type="reset" class="btn btn-default" style="width: 18%">Cancel</button>
											<button type="submit" id="submit2" name="submit2" class="btn btn-primary" style="width: 17%; background-color:#629bf0; border-color:#7dacf2">Submit</button>
										</div>
									</div>
								</div>

							</div>
						</div>
					</div>
				</div>

<!----------------------------right colum------------------------------>
				<div class="col-lg-6">
					<?php if($_POST["submit"]&&!$err&&!$errSequence) {?>
						<div class="panel panel-primary" style="height:659px;border-color:#7dacf2">
							<div class="panel-heading" style="background-color:#629bf0; border-color:#7dacf2">
								<h3 class="panel-title">Single Site Prediction</h3>
							</div>
							<div class="panel-body">
								<div class="form-group">
									<div class="col-lg-12"><?php echo "<p style='font-size:20px; font-weight: bold; color:#4D5360'>Protein Stability Change: <a href='http://sysbio.rnet.missouri.edu/dnpros/bin/Single/".$ssss."/".$original.$position.$mutated.".txt' style='text-decoration:none'><span style='color:".$color. "; font-size:30px'>$result</span></a></p>" ?>
										<div class="col-lg-12">
											<div class='progress'>
												<?php echo "<div class='progress-bar' aria-valuemax='100' aria-valuemin='0' aria-valuenow='$value'style='background-image: none; background-color: #46BFBD'>$value</div>"?>
												<?php echo "<div class='progress-bar' aria-valuemax='100' aria-valuemin='0' aria-valuenow='".(100-$value)."' style='width: 100%; background-image: none; background-color: #F7464A'>".(100-$value). "</div>"?>
											</div>
										</div>
										<!--<div class="col-lg-2">
											<div class="progresslabel"> <strong><?php echo "<span style='color:$color'>$value%</span>"?></strong></div><br>
										</div>-->
										<div class="col-lg-12">
											<p style="font-size:16px; color:#4D5360">The probability of <span style="color: #46BFBD">increasing</span> and <span style="color: #F7464A">decreasing</span> protein stability.</p>
											<br>
										</div>
										<br>
										<ul class="list-group">
											<p style="font-size:20px; font-weight: bold; color:#4D5360" class="list-group-item-heading">Mutation Information</p>
											<div class="col-lg-12">
											<a  class="list-group-item">
												<?php echo "<span class='badge' style='background-color: $color'>$position</span><p style='font-size:18px'><font style='color:#4D5360'>Mutation Position</font></p>"; ?>
												<?php echo "<span class='badge' style='background-color: $color'>$original</span><p style='font-size:18px'><font style='color:#4D5360'>Original Amino Acid</font></p>"; ?>
												<?php echo "<span class='badge' style='background-color: $color'>$mutated</span><p style='font-size:18px'><font style='color:#4D5360'>Mutated Amino Acid</font></p>"; ?>
											</a>
											<a  class="list-group-item">
												<h4 class="list-group-item-heading" style="color:#4D5360">Sequence</h4>
												
												<p class="list-group-item-text" id="wrap" style="text-align:justify"><font color="#949FB1"><?php echo $sequence;?></font></p>
											</a>
											<br>
											<br>
											</div>
										</ul>
												
									</div>
								</div>
							</div>
						</div>
					<?php } else if($_POST["submit1"]&&!$errSeq) { ?>
						<div class="panel panel-primary" style="border-color:#7dacf2">
							<div class="panel-heading" style="background-color:#629bf0; border-color:#7dacf2">
								<h3 class="panel-title">Sequence Comparison</h3>
							</div>
							<div class="panel-body">
								<div class="form-group">
									<br>
									<div class="col-lg-12">
										<div class="col-lg-6">
											<div id="canvas-holder">
												<canvas id="chart-area" width="350" height="350"/>
											</div>
										</div>
										<div class="col-lg-6">
											<br>
											<p style="color:#4D5360"><span style="font-size:25px; font-weight: bold"><?php echo $num."\t" ?></span>mutations in total</p>
											<p style="color:#4D5360"><span style="font-size:25px; font-weight: bold"><font color="#46BFBD"><?php echo $positive."\t" ?></font></span> increase protein stability</p>
											<p style="color:#4D5360"><span style="font-size:25px; font-weight: bold"><font color="#F7464A"><?php echo $num-$positive."\t" ?></font></span> decrease protein stability</p>
											<p style="color:#4D5360"><span style="font-size:25px; font-weight: bold"><font color="#FDB45C"><?php echo $per."%\t" ?></font></span> instances is positive</p>
										</div>
									</div>
								</div>
								<div class="col-lg-12">
									<div class="list-group">
										<a class="list-group-item">
											<h4 class="list-group-item-heading" style="color:#4D5360">Original Sequence</h4>
											<?php echo "<p id='wrp'><font color='#949FB1'>$oriSeq</font></p>"?>
										</a>
										<a  class="list-group-item" href="<?php echo "http://sysbio.rnet.missouri.edu/dnpros/bin/Sequence/".$t."/mutation.txt" ?>">
											<h4 class="list-group-item-heading" style="color:#4D5360">Mutation <span style="font-size:13px">(the posibility of <font color="#46BFBD">increasing</font> and <font color="#F7464A">decreasing</font> protein stability)</span></h4>
											<br>
											<div style="height:200px; overflow-x: hidden; overflow-y: scroll">
												<?php 
													foreach($test as $t => $val) {
												?>
												<div>
													<div class="col-lg-2">
														<?php echo "<p style='color:#4D5360'>$t</p>" ?>
													</div>
													<div class="col-lg-9">
														<div class="progress">
															<?php echo "<div class='progress-bar' aria-valuemax='100' aria-valuemin='0' aria-valuenow='$val' style='background-image: none; background-color: #46BFBD'>$val</div>"?>
															<?php echo "<div class='progress-bar' aria-valuemax='100' aria-valuemin='0' aria-valuenow='". (100-$val). "' style='width:100%; background-image: none; background-color: #F7464A'>".(100-$val)."</div>";?>
															
														</div>

													</div>
													<div class="col-lg-1">
														<?php 
															if($val>=50) {
																echo "<p style='color:#46BFBD; font-size:14px'><strong>P</strong></p>";
															} else {
																echo "<p style='color:#F7464A; font-size:14px'><strong>N</strong></p>";
															}
														?>
													</div>
												</div>
												<?php } ?>
											</div>
										</a>
									</div>
								</div>
							</div>
						</div>

					<?php } else { ?>
						<div id="DBN" class="panel panel-primary" style="height:659px; border-color:#7dacf2">
							<div class="panel-heading" style="background-color:#629bf0; border-color:#7dacf2">
								<h3 class="panel-title">Detail</h3>
							</div>
							<div class="panel-body">
								<div class="form-group">
								<div class="col-lg-12">
									<p style="text-align:justify; color:#4D5360">&nbsp;&nbsp;&nbsp;&nbsp; As a method of Deep Learning, Deep Belief Network forward propagate first. Unlike a common Neural Network, DBN produces a better set of weights and biases by stacking several RBMs layer by layer instead of setting them randomly. This is called pre-training of a DBN. After that, DBN is fine-tuned by back-propagation which is the same as a regular Neural Network. <br><br>&nbsp;&nbsp;&nbsp;&nbsp;<strong>Following gif shows how a regular DBN works.</strong></p>
									<img src="DBN.gif" alt="Deep Belief Network" style="width:500px;height:450px">
								</div>
								</div>

								
							</div>
						</div>


					<?php } ?>


					<script>
						var bar = $('.progress-bar');
						$(function(){
							$(bar).each(function(){
								bar_width = $(this).attr('aria-valuenow');
								$(this).width(bar_width + '%');
							});
						});
						
						var doughnutData = [
							{
								value: <?php echo $num-$positive ?>,
								color:"#F7464A",
								highlight: "#FF5A5E",
								label: "Negative"
							},
							{
								value: <?php echo $positive ?>,
								color: "#46BFBD",
								highlight: "#5AD3D1",
								label: "Positive"
							}

						];

						window.onload = function(){
							var ctx = document.getElementById("chart-area").getContext("2d");
							window.myDoughnut = new Chart(ctx).Doughnut(doughnutData, {responsive : true});
						};
				</script>



				</div>
			</div>
		</div>
		<br>
		<br>
		<br>
		<br>

		<hr />
		<p style="text-align:right">Department of Computer Science&nbsp;&nbsp;&nbsp;&nbsp;<br>University of Missouri - Columbia&nbsp;&nbsp;&nbsp;&nbsp;</p>
		<p style="text-align:right"><a href="about.htm">Contact&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></p>
	</body>


	<script>

		$("#file-0").fileinput({
			'allowedFileExtensions' : ['txt'],
		});
		$("#file-1").fileinput({
			initialPreview: ["<div src='sample.txt' class='file-preview-text'></div>"],

			initialPreviewConfig: [
				{caption: 'sample.txt', width:'160px', url: '#'},
			],
			uploadUrl: '#',
			allowedFileExtensions : ['txt'],
			overwriteInitial: false,
			maxFileSize: 1000,
			maxFilesNum: 10,
			slugCallback: function(filename) {
				return filename.replace('(', '_').replace(']', '_');
			}
		});
		
		

	</script>
</html>

