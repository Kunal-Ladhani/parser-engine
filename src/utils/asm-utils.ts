import { SecretsManager } from 'aws-sdk';
import { logger } from './logger';

const secretId = process.env.AWS_S3_ARN || '';

export const initASM = async () => {
	const asm = new SecretsManager({ region: 'ap-south-1' });
	const secrets = asm.getSecretValue({ SecretId: secretId }).promise();
	logger.info(`ASM Secrets: ${JSON.stringify(secrets)}`);
};
