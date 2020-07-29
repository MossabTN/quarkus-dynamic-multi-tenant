create sequence hibernate_sequence;

create table if not exists task(
    id               bigint       not null constraint task_pkey primary key,
    title            varchar(255) not null,
    created_by       varchar(255) not null
);

INSERT INTO "public"."task" ("id", "title","created_by") VALUES (1, 'task for admin', 'admin');
INSERT INTO "public"."task" ("id", "title","created_by") VALUES (2, 'task for user', 'user');
