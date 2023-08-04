--------------------------------------------------------
--  File created - Wednesday-December-09-2020
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table VENDOR_RATE
--------------------------------------------------------

  CREATE TABLE "COMPASS_MI"."VENDOR_RATE"
   (	"GUID" RAW(16),
	"VERSION" NUMBER(10,0),
	"PTS_SERVICE_COMBO" RAW(16),
	"START_DATE" TIMESTAMP (6),
	"DEFAULT_UNITS" NUMBER(8,2),
	"DEFAULT_COST_PER_UNIT" NUMBER(10,2),
	"VENDOR_GUID" RAW(16),
	"STOP_DATE" TIMESTAMP (6),
	"AGENT_CUSTOM_SERVICE" RAW(16)
   ) ;
--------------------------------------------------------
--  DDL for Index UK_STJ0X2DSH6HOED306NUO9DPVB
--------------------------------------------------------

  CREATE UNIQUE INDEX "COMPASS_MI"."UK_STJ0X2DSH6HOED306NUO9DPVB" ON "COMPASS_MI"."VENDOR_RATE" ("PTS_SERVICE_COMBO", "START_DATE", "DEFAULT_UNITS", "DEFAULT_COST_PER_UNIT", "AGENT_CUSTOM_SERVICE", "VENDOR_GUID") ;
--------------------------------------------------------
--  DDL for Index IDXVENDOR_RATEVENDOR
--------------------------------------------------------

  CREATE INDEX "COMPASS_MI"."IDXVENDOR_RATEVENDOR" ON "COMPASS_MI"."VENDOR_RATE" ("PTS_SERVICE_COMBO", "VENDOR_GUID", "AGENT_CUSTOM_SERVICE") ;
--------------------------------------------------------
--  DDL for Index SYS_C0025177
--------------------------------------------------------

  CREATE UNIQUE INDEX "COMPASS_MI"."SYS_C0025177" ON "COMPASS_MI"."VENDOR_RATE" ("GUID") ;
--------------------------------------------------------
--  Constraints for Table VENDOR_RATE
--------------------------------------------------------

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" MODIFY ("GUID" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" MODIFY ("VERSION" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" MODIFY ("PTS_SERVICE_COMBO" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" MODIFY ("START_DATE" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" MODIFY ("VENDOR_GUID" NOT NULL ENABLE);

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" ADD PRIMARY KEY ("GUID");

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" ADD CONSTRAINT "UK_STJ0X2DSH6HOED306NUO9DPVB" UNIQUE ("PTS_SERVICE_COMBO", "START_DATE", "DEFAULT_UNITS", "DEFAULT_COST_PER_UNIT", "AGENT_CUSTOM_SERVICE", "VENDOR_GUID");
--------------------------------------------------------
--  Ref Constraints for Table VENDOR_RATE
--------------------------------------------------------

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" ADD CONSTRAINT "FK_2OJCWIWFKGLBMAMA744QQ3J2H" FOREIGN KEY ("VENDOR_GUID")
	  REFERENCES "COMPASS_MI"."VENDOR" ("GUID") ENABLE;

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" ADD CONSTRAINT "FK_A7FN2V5R7ORTLJB5HIR3QHPDT" FOREIGN KEY ("PTS_SERVICE_COMBO")
	  REFERENCES "COMPASS_MI"."PTS_SERVICE_COMBO" ("GUID") ENABLE;

  ALTER TABLE "COMPASS_MI"."VENDOR_RATE" ADD CONSTRAINT "FK_SAADQIWG90YF4QD4VWL8WTS0J" FOREIGN KEY ("AGENT_CUSTOM_SERVICE")
	  REFERENCES "COMPASS_MI"."AGENT_CUSTOM_SERVICE" ("GUID") ENABLE;
