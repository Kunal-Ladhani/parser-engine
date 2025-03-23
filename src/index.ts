import bodyParser from 'body-parser';
import cors from 'cors';
import dotenv from 'dotenv';
import express, { Application } from 'express';

import { errorMiddleware, loggerMiddleware, notFoundMiddleware } from './middlewares';
import { fileRouter } from './routes';
import { logger } from './utils';

const ENV = process.env.NODE_ENV || 'dev';

// Load the correct .env file based on NODE_ENV
dotenv.config({ path: `.env.${ENV}` });

const PORT = process.env.PORT!;
const HOSTNAME = process.env.HOSTNAME!;

console.log('AWS_ACCESS_KEY_ID:', process.env.AWS_ACCESS_KEY_ID);
console.log('AWS_SECRET_ACCESS_KEY:', process.env.AWS_SECRET_ACCESS_KEY);
console.log('AWS_REGION:', process.env.AWS_REGION);

const app: Application = express();

app.use(cors());
app.use('*', cors());

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

app.use(bodyParser.text());
app.use(bodyParser.json({ limit: '10mb' }));
app.use(bodyParser.urlencoded({ limit: '10mb', extended: true }));

// Log all incoming requests
app.use(loggerMiddleware);

// map all routes to appropriate routers
app.use('/files', fileRouter);
//...

// 404 Not Found Middleware
app.use(notFoundMiddleware);

// Error Handler Middleware (must be the last middleware)
app.use(errorMiddleware);

app.listen(PORT, () => {
	logger.info(`Server is running on http://${HOSTNAME}:${PORT}`);
});

export { app };
