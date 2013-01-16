<?php 


	use chardev\backend\Database;

use chardev\backend\DatabaseHelper;

require_once __DIR__ . '/../Autoloader.php';
	
	$db = Database::getConnection();
	
	$records = DatabaseHelper::fetchMany( $db, "SELECT * FROM chardev_user.`battlenetprofile` WHERE `ID` > 913");
	
	foreach( $records as $record ) {
	
		try {
			$profile = new \chardev\profiles\BattleNetProfile( $record['Name'], $record['Realm'], $record['Region'] );
		}
		catch ( \Exception $e ) {
			continue;
		}
		
		$updateStmt = $db->prepare("
				UPDATE chardev_user.`battlenetprofile`
				SET `CharacterRaceID`=? WHERE `ID`=?"
		);
		
		$updateStmt->execute(array(
				$profile->getCharacterRaceId(),
				$record['ID']
		));
		
		echo $record['ID'] . "\n";
			
		DatabaseHelper::testStatement($updateStmt);
	}
?>