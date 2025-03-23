import { Request, Response } from 'express';

import { uploadFileToS3 } from '../services';

// Upload File API
const uploadFile = async (req: Request, res: Response): Promise<any> => {
	if (!req.file) {
		return res.status(400).json({ message: 'No file uploaded' });
	}

	const resp = await uploadFileToS3({
		key: req.file.originalname,
		file: req.file.buffer,
	});

	const successResponse = {
		message: 'File uploaded successfully',
		...resp,
	};

	res.status(201).json(successResponse);
};

// Download File API
const downloadFile = (req: Request, res: Response): any => {
	const { filename } = req.params;
	res.download('', filename, (err) => {
		if (err) {
			res.status(500).json({ message: 'Error downloading file' });
		}
	});
};

export { downloadFile, uploadFile };
