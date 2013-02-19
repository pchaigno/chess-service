<?php

if(isset($_FILES['file'])) {
	$fichier = basename($_FILES['file']['name']);
	if(filesize($_FILES['file']['tmp_name'])>100000) {
		exit('tsss...');
	}
	if(move_uploaded_file($_FILES['file']['tmp_name'], 'moves.txt')) {
		echo 'Upload effectué avec succès !';
	} else {
		echo 'Echec de l\'upload !';
	}
}

?>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8"/>
        <title>Upload</title>
    </head>
    <body>
		<form action="upload.php" method="POST" enctype="multipart/form-data">
			<input type="hidden" name="MAX_FILE_SIZE" value="100000">
			<input type="file" name="file" id="file"/><br/>
			<input type="submit" name="upload" value="Uploader"/>
		</form>
    </body>
</html>