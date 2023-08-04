--------------------------------------------------------
--  File created - Thursday-March-11-2021   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table AGENT_CUSTOM_SERVICE
--------------------------------------------------------

  CREATE TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" 
   (	"GUID" RAW(16), 
	"VERSION" NUMBER(10,0), 
	"PTS_SERVICE_COMBO" RAW(16), 
	"DESCRIPTION" VARCHAR2(50 CHAR), 
	"AGENT_GUID" RAW(16), 
	"ACTIVE" VARCHAR2(1 CHAR) DEFAULT 0
   ) ;
--------------------------------------------------------
--  DDL for Index UK_IGWLRN2WBJJ85R682T5HNAHFK
--------------------------------------------------------

  CREATE UNIQUE INDEX "COMPASS_MI"."UK_IGWLRN2WBJJ85R682T5HNAHFK" ON "COMPASS_MI"."AGENT_CUSTOM_SERVICE" ("PTS_SERVICE_COMBO", "DESCRIPTION", "AGENT_GUID") ;
--------------------------------------------------------
--  DDL for Index IDXAGENT_CUSTOM_SERVICEAGENT
--------------------------------------------------------

  CREATE INDEX "COMPASS_MI"."IDXAGENT_CUSTOM_SERVICEAGENT" ON "COMPASS_MI"."AGENT_CUSTOM_SERVICE" ("PTS_SERVICE_COMBO", "AGENT_GUID") ;
--------------------------------------------------------
--  DDL for Index SYS_C0015266
--------------------------------------------------------

  CREATE UNIQUE INDEX "COMPASS_MI"."SYS_C0015266" ON "COMPASS_MI"."AGENT_CUSTOM_SERVICE" ("GUID") ;
--------------------------------------------------------
--  Constraints for Table AGENT_CUSTOM_SERVICE
--------------------------------------------------------

  ALTER TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" MODIFY ("GUID" NOT NULL ENABLE);
 
  ALTER TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" MODIFY ("VERSION" NOT NULL ENABLE);
 
  ALTER TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" MODIFY ("PTS_SERVICE_COMBO" NOT NULL ENABLE);
 
  ALTER TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" MODIFY ("AGENT_GUID" NOT NULL ENABLE);
 
  ALTER TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" MODIFY ("ACTIVE" NOT NULL ENABLE);
 
  ALTER TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" ADD PRIMARY KEY ("GUID");
 
  ALTER TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" ADD CONSTRAINT "UK_IGWLRN2WBJJ85R682T5HNAHFK" UNIQUE ("PTS_SERVICE_COMBO", "DESCRIPTION", "AGENT_GUID");
--------------------------------------------------------
--  Ref Constraints for Table AGENT_CUSTOM_SERVICE
--------------------------------------------------------

  ALTER TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" ADD CONSTRAINT "FK_5U6L1K3BIWPN8EK5N9OHXTWXF" FOREIGN KEY ("AGENT_GUID")
	  REFERENCES "COMPASS_MI"."AGENT" ("GUID") ENABLE;
 
  ALTER TABLE "COMPASS_MI"."AGENT_CUSTOM_SERVICE" ADD CONSTRAINT "FK_6NAPF6V18JO3LW6R40A1D2M75" FOREIGN KEY ("PTS_SERVICE_COMBO")
	  REFERENCES "COMPASS_MI"."PTS_SERVICE_COMBO" ("GUID") ENABLE;