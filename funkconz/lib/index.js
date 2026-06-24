"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.onDokyumentReten = void 0;
const firestore_1 = require("firebase-functions/v2/firestore");
const admin = __importStar(require("firebase-admin"));
const storage_1 = require("@google-cloud/storage");
admin.initializeApp();
const storage = new storage_1.Storage();
exports.onDokyumentReten = (0, firestore_1.onDocumentWritten)("users/{userId}/layouts/{env}", async (event) => {
    var _a, _b, _c, _d, _e;
    const after = (_b = (_a = event.data) === null || _a === void 0 ? void 0 : _a.after) === null || _b === void 0 ? void 0 : _b.data();
    if (!after)
        return;
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
            await ((_e = (_d = (_c = event.data) === null || _c === void 0 ? void 0 : _c.after) === null || _d === void 0 ? void 0 : _d.ref) === null || _e === void 0 ? void 0 : _e.update({ ezRepleys: admin.firestore.FieldValue.delete() }));
        }
        catch (error) {
            console.error("Error backing up layout:", error);
        }
    }
});
//# sourceMappingURL=index.js.map