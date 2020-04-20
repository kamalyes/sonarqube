CREATE TABLE "ISSUE_CHANGES"(
    "ID" BIGINT NOT NULL AUTO_INCREMENT (1,1),
    "UUID" VARCHAR(40) NOT NULL,
    "KEE" VARCHAR(50),
    "ISSUE_KEY" VARCHAR(50) NOT NULL,
    "USER_LOGIN" VARCHAR(255),
    "CHANGE_TYPE" VARCHAR(20),
    "CHANGE_DATA" CLOB(2147483647),
    "CREATED_AT" BIGINT,
    "UPDATED_AT" BIGINT,
    "ISSUE_CHANGE_CREATION_DATE" BIGINT
);
ALTER TABLE "ISSUE_CHANGES" ADD CONSTRAINT "PK_ISSUE_CHANGES" PRIMARY KEY("ID");
CREATE INDEX "ISSUE_CHANGES_ISSUE_KEY" ON "ISSUE_CHANGES"("ISSUE_KEY");
CREATE INDEX "ISSUE_CHANGES_KEE" ON "ISSUE_CHANGES"("KEE");
