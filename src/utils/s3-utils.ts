import { S3 } from 'aws-sdk';

import { IS3FileUploadRequest, IS3FileUploadResponse } from '../interfaces';
import { logger } from '../utils';

const s3Client = new S3({
	region: process.env.AWS_REGION!,
	accessKeyId: process.env.AWS_ACCESS_KEY_ID!,
	secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY!,
});

const uploadToS3 = async (s3FileUploadRequest: IS3FileUploadRequest): Promise<IS3FileUploadResponse> => {
	const { key, body, bucketName } = s3FileUploadRequest;

	const putObjectRequest: S3.PutObjectRequest = {
		Key: key,
		Bucket: bucketName,
		Body: body,
	};

	try {
		const putObjectResponse = await s3Client.upload(putObjectRequest).promise();
		logger.info(`PUT response from S3: ${JSON.stringify(putObjectResponse)}`);
		return {
			Key: putObjectResponse.Key,
			ETag: putObjectResponse.ETag,
			Location: putObjectResponse.Location,
			Bucket: putObjectResponse.Bucket,
		};
	} catch (err: any) {
		logger.error(`Error from S3 while uploading object to ${bucketName} :: ${err?.message}`);
		throw err;
	}
};

export { uploadToS3 };
