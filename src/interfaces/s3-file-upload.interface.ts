export interface IS3FileUploadRequest {
	key: string;
	body: Buffer;
	bucketName: string;
}

export interface IS3FileUploadResponse {
	ETag: string;
	Location: string;
	Key: string;
	Bucket: string;
}

export interface IUploadFileToS3Request {
	file: any;
	key: string;
}

export interface IUploadFileToS3Response {
	ETag: string;
	Location: string;
}
