--ALTER TABLE COMPASS_MI.VENDOR DROP PRIMARY KEY CASCADE;
--
--DROP TABLE COMPASS_MI.VENDOR CASCADE CONSTRAINTS;


CREATE TABLE "COMPASS_MI"."VENDOR"
   (	"GUID" RAW(16) NOT NULL ENABLE,
	"VERSION" NUMBER(10,0) NOT NULL ENABLE,
	"CODE" VARCHAR2(10 CHAR) NOT NULL ENABLE,
	"AGENT_GUID" RAW(16) NOT NULL ENABLE,
	"VENDOR_TYPE" VARCHAR2(1 CHAR) NOT NULL ENABLE,
	"ASSESSMENT_REPORT" VARCHAR2(1 CHAR),
	"SERVICE_SCHEDULE_ENABLED" VARCHAR2(1 CHAR),
	"BILLING_ENABLED" VARCHAR2(1 CHAR),
	"VENDOR_VIEW_ENABLED" VARCHAR2(1 CHAR),
	"NAME" VARCHAR2(50 CHAR) NOT NULL ENABLE,
	"FIRST_NAME" VARCHAR2(20 CHAR),
	"MIDDLE_INITIAL" VARCHAR2(1 CHAR),
	"SSN" VARCHAR2(11 CHAR),
	"PTS_TAXONOMY" RAW(16),
	"FEDERAL_ID" VARCHAR2(10 CHAR),
	"NATIONAL_PROVIDER_ID" VARCHAR2(10 CHAR),
	"ADDRESS_1" VARCHAR2(50 CHAR),
	"ADDRESS_2" VARCHAR2(50 CHAR),
	"CITY" VARCHAR2(30 CHAR),
	"STATE" VARCHAR2(2 CHAR),
	"ZIP" VARCHAR2(5 CHAR),
	"ZIP_4" VARCHAR2(4 CHAR),
	"PHONE" VARCHAR2(14 CHAR),
	"TOLL_FREE_PHONE" VARCHAR2(14 CHAR),
	"FAX_PHONE" VARCHAR2(14 CHAR),
	"WEBSITE" VARCHAR2(50 CHAR),
	"LICENSE" VARCHAR2(15 CHAR),
	"MEETS_ADA_ACCESSIBILITY" CLOB,
	"WORKER_LANGUAGES" CLOB,
	"EXPERIENCE_AREAS" CLOB,
	"RESIDENTIAL_SETTING" VARCHAR2(1 CHAR),
	"PROBATION" VARCHAR2(1 CHAR) DEFAULT 0 NOT NULL ENABLE,
	"HOLD_PAYMENT" VARCHAR2(1 CHAR) DEFAULT 0 NOT NULL ENABLE,
	"CORPORATE_STATUS" VARCHAR2(1 CHAR),
	"MINORITY_OWNED" VARCHAR2(1 CHAR) DEFAULT 0 NOT NULL ENABLE,
	"ACCEPTS_NEW_ENROLLEES" VARCHAR2(1 CHAR) DEFAULT 1 NOT NULL ENABLE,
	"INCLUDE_IN_PROVIDER_DIRECTORY" VARCHAR2(1 CHAR) DEFAULT 1 NOT NULL ENABLE,
	"PROBATION_START_DATE" TIMESTAMP (6),
	"PROBATION_STOP_DATE" TIMESTAMP (6),
	"LAST_MONITORING_VISIT" TIMESTAMP (6),
	"DAYS_HOURS_OPERATION" VARCHAR2(50 CHAR),
	"PROVIDER_DIRECTORY_NOTES" CLOB,
	"INTERNAL_NOTES" CLOB);

CREATE INDEX COMPASS_MI.IDXVENDORAGENT ON COMPASS_MI.VENDOR(AGENT_GUID, PTS_TAXONOMY);

CREATE UNIQUE INDEX COMPASS_MI.UK_1QFKN4LJTX5RFEE8KDWWDHE5M ON COMPASS_MI.VENDOR(CODE, AGENT_GUID);


ALTER TABLE COMPASS_MI.VENDOR ADD PRIMARY KEY (GUID);
ALTER TABLE COMPASS_MI.VENDOR ADD CONSTRAINT UK_1QFKN4LJTX5RFEE8KDWWDHE5M UNIQUE (CODE, AGENT_GUID);

ALTER TABLE COMPASS_MI.VENDOR ADD (  CONSTRAINT FK_2X2C9QLARU9IWFS34DX8ND8RH
 FOREIGN KEY (PTS_TAXONOMY)  REFERENCES COMPASS_MI.PTS_TAXONOMY (GUID),
  CONSTRAINT FK_LRYCQEYTR1I5BE6EL5RS9CG7G  FOREIGN KEY (AGENT_GUID)
 REFERENCES COMPASS_MI.AGENT (GUID));
