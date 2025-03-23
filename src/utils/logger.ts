import winston from "winston";

const LOG_LEVEL = process.env.LOG_LEVEL || 'info';

const logger = winston.createLogger({
  level: LOG_LEVEL,
  format: winston.format.combine(
    winston.format.timestamp({ format: "DD-MM-YYYY hh:mm:ss a" }),
    winston.format.json(),
    winston.format.prettyPrint({ colorize: true }),
  ),
  transports: [
    new winston.transports.Console(), // Logs to console
    new winston.transports.File({ filename: "logs/error.log", level: "error" }), // Error logs
    new winston.transports.File({ filename: "logs/combined.log" }), // All logs
  ],
});

export { logger };
