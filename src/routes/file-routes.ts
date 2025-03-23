import express from 'express';

import multer from 'multer';

import { downloadFile, uploadFile } from '../controllers/file-controller';

const fileRouter = express.Router();

const storage = multer.memoryStorage(); // Store file in memory before uploading

const upload = multer({
  storage,
  limits: { fileSize: 10 * 1024 * 1024 }, // Limit: 5MB
});

fileRouter.post('/upload', upload.single("file") ,  uploadFile);

fileRouter.get('/download/:filename', downloadFile);

export { fileRouter };
