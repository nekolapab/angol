import { onDocumentWritten } from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";
import { Storage } from "@google-cloud/storage";

admin.initializeApp();
const storage = new Storage();

export const onDokyumentReten = onDocumentWritten("users/{userId}/layouts/{env}", async (event) => {
    const after = event.data?.after?.data();
    if (!after) return;

    if (after.ezRepleys === true) {
      const { userId, env } = event.params;
      const bucketName = "angol-leyawts-bakup";
      const fileName = `backups/${userId}/${env}_${Date.now()}.json`;

      try {
        const bucket = storage.bucket(bucketName);
        const file = bucket.file(fileName);
        const data = JSON.stringify(after);

        await file.save(data, {
          contentType: "application/json"
        });

        console.log(`Successfully backed up ${env} for ${userId} to ${fileName}`);
        
        // Remove the flag after backup
        await event.data?.after?.ref?.update({ ezRepleys: admin.firestore.FieldValue.delete() });
      } catch (error) {
        console.error("Error backing up layout:", error);
      }
    }
});
