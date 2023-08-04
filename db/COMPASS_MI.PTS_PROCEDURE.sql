
--ALTER TABLE COMPASS_MI.PTS_PROCEDURE DROP PRIMARY KEY CASCADE;
--
--DROP TABLE COMPASS_MI.PTS_PROCEDURE CASCADE CONSTRAINTS PURGE;
--------------------------------------------------------
--  File created - Tuesday-June-30-2020
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table PTS_PROCEDURE
--------------------------------------------------------

  CREATE TABLE "COMPASS_MI"."PTS_PROCEDURE"
   (	"GUID" RAW(16),
	"VERSION" NUMBER(10,0),
	"CODE" VARCHAR2(5 CHAR),
	"PTS_GUID" RAW(16),
	"DESCRIPTION" VARCHAR2(150 CHAR),
	"PTS_UNIT" RAW(16)
   ) ;
--------------------------------------------------------
--  DDL for Index IDXPTS_PROCEDUREPTS
--------------------------------------------------------

  CREATE INDEX "COMPASS_MI"."IDXPTS_PROCEDUREPTS" ON "COMPASS_MI"."PTS_PROCEDURE" ("PTS_GUID", "PTS_UNIT") ;
--------------------------------------------------------
--  DDL for Index SYS_C0020086
--------------------------------------------------------

  CREATE UNIQUE INDEX "COMPASS_MI"."SYS_C0020086" ON "COMPASS_MI"."PTS_PROCEDURE" ("GUID") ;
--------------------------------------------------------
--  DDL for Index UK_9GHJ6SYSD4N5S9030Q7MN52OM
--------------------------------------------------------

  CREATE UNIQUE INDEX "COMPASS_MI"."UK_9GHJ6SYSD4N5S9030Q7MN52OM" ON "COMPASS_MI"."PTS_PROCEDURE" ("CODE", "PTS_GUID");
--------------------------------------------------------
--  Constraints for Table PTS_PROCEDURE
--------------------------------------------------------

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" MODIFY ("GUID" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" MODIFY ("VERSION" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" MODIFY ("CODE" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" MODIFY ("PTS_GUID" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" MODIFY ("DESCRIPTION" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" MODIFY ("PTS_UNIT" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" ADD PRIMARY KEY ("GUID");

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" ADD CONSTRAINT "UK_9GHJ6SYSD4N5S9030Q7MN52OM" UNIQUE ("CODE", "PTS_GUID");
--------------------------------------------------------
--  Ref Constraints for Table PTS_PROCEDURE
--------------------------------------------------------

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" ADD CONSTRAINT "FK_18PFTFC7PGBKQRMCJH42R8PUH" FOREIGN KEY ("PTS_UNIT")
	  REFERENCES "COMPASS_MI"."PTS_UNIT" ("GUID") ENABLE;

  ALTER TABLE "COMPASS_MI"."PTS_PROCEDURE" ADD CONSTRAINT "FK_I53QSH21VWYF8J5FMKWQ4BOAX" FOREIGN KEY ("PTS_GUID")
	  REFERENCES "COMPASS_MI"."PTS" ("GUID") ENABLE;
