create table USERS (ID IDENTITY, NAME VARCHAR NOT NULL, COMPANY_ID INTEGER);
create table COMPANIES (ID INTEGER NOT NULL, NAME VARCHAR NOT NULL);

alter table USERS ADD CONSTRAINT IDX_USERS_FK0 FOREIGN KEY (COMPANY_ID) REFERENCES COMPANIES (ID);
alter table COMPANIES ADD CONSTRAINT IDX_COMPANIES_PK PRIMARY KEY (ID);

insert into COMPANIES values (1, 'Biz Reach');
insert into COMPANIES values (2, 'Recruit');
insert into COMPANIES values (3, 'DODA');

insert into USERS(NAME, COMPANY_ID) values ('Naoki Takezoe', 1);
insert into USERS(NAME) values ('Takako Shimamoto');
