import {
	IS3FileUploadRequest,
	IS3FileUploadResponse,
	IUploadFileToS3Request,
	IUploadFileToS3Response,
} from '../interfaces';

import { logger, uploadToS3 } from '../utils';

const uploadFileToS3 = async (uploadFileToS3Request: IUploadFileToS3Request): Promise<IUploadFileToS3Response> => {
	const bucketName = process.env.AWS_S3_BUCKET!;
	const key = uploadFileToS3Request.key;

	try {
		const body = Buffer.from(uploadFileToS3Request.file, 'base64');
		const s3FileUploadRequest: IS3FileUploadRequest = {
			body,
			bucketName,
			key,
		};
		const response: IS3FileUploadResponse = await uploadToS3(s3FileUploadRequest);
		logger.info(`Upload to S3 response: ${JSON.stringify(response)}`);
		return { ...response };
	} catch (err: any) {
		logger.error(`Error from S3 while uploading file to ${bucketName} :: ${err?.message}`);
		throw err;
	}
};

export { uploadFileToS3 };
