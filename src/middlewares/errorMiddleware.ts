import { Request, Response, NextFunction } from "express";
import { IAppError } from "../interfaces";

const errorMiddleware = (
  err: IAppError,
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  const statusCode = err.statusCode || 500;
  const message = err.message || "Internal Server Error";

  res.status(statusCode).json({
    success: false,
    message,
    stack: process.env.NODE_ENV === "production" ? undefined : err.stack, // Hide stack trace in production
  });
};

export { errorMiddleware };
