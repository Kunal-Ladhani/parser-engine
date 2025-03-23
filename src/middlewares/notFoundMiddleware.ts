import { NextFunction, Request, Response } from "express";
import { logger } from "../utils";

const notFoundMiddleware = (
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  logger.warn(`[404] ${req.method} ${req.originalUrl}`);
  const errorMessage = { message: "The requested route does not exist." };
  res.status(404).json(errorMessage);
};

export { notFoundMiddleware };
