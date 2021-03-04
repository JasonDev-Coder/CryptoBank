<?php
session_start();
if (!isset($_SESSION["isLogged"])) {
  header("Location:/CryptoBank/scripts/public");
}
?>

<html>
    <head>
        <title>Reply</title>
        <link rel="stylesheet" href="/CryptoBank/scripts/css/replyMessage.css" />
    </head>
    <body>
        <div class="replyForm">
        <p>To:</p> <input type="email" name="destination"  value="<?php echo $_GET['email']; ?>" readonly/><br>
        <p>Message:</p><textarea rows='8'></textarea><br>
        <?php
        echo "<a href='/CryptoBank/scripts/public/MessageController/sendReply/".$_GET['ID']."'><input type='submit' value='Reply'></a>";
        ?>
        </div>
    </body>
</html>
