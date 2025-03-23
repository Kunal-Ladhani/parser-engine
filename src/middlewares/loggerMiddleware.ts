import { NextFunction, Request, Response } from "express";
import { logger } from '../utils';

const loggerMiddleware = (req: Request, res: Response, next: NextFunction) => {
    logger.info(`[${req.method}] ${req.originalUrl}`);
    next();
};

export { loggerMiddleware };