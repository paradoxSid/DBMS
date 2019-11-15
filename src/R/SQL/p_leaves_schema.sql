CREATE TABLE allLeaves(
	l_id INT NOT NULL,
	f_id INT NOT NULL,
	applicant VARCHAR(20) NOT NULL,
	application_status VARCHAR(50),
	notifyUser BOOLEAN NOT NULL,
	borrowLeaves INT NOT NULL,

	PRIMARY KEY(l_id)
);
 
CREATE TABLE leaveRoutes(
	applicant VARCHAR(20) NOT NULL,
	auth1 VARCHAR(20) NOT NULL,
	auth2 VARCHAR(20) NOT NULL,

	PRIMARY KEY(applicant)
);

CREATE TABLE newLeaves(
	l_id INT NOT NULL,
	application_date DATE NOT NULL,
	f_id INT NOT NULL,
	d_id VARCHAR(20) NOT NULL,
	from_date DATE NOT NULL,
	to_date DATE NOT NULL,
	commentsFac VARCHAR(1000),

	PRIMARY KEY (l_id),
	FOREIGN KEY(l_id) REFERENCES allLeaves(l_id)
);

CREATE TABLE rejectedLeaves(
	l_id INT NOT NULL,
	application_date DATE NOT NULL,
	f_id INT NOT NULL,
	d_id VARCHAR(20) NOT NULL,
	from_date DATE NOT NULL,
	to_date DATE NOT NULL,
	commentsFac VARCHAR(1000),
    auth VARCHAR(20) NOT NULL,
    authId INT NOT NULL,
    authComments VARCHAR(1000),
	authResponseTime DATE NOT NULL,

	PRIMARY KEY (l_id),
	FOREIGN KEY(l_id) REFERENCES allLeaves(l_id)
);

CREATE TABLE approved1Leaves(
	l_id INT NOT NULL,
	application_date DATE NOT NULL,
	f_id INT NOT NULL,
	d_id VARCHAR(20) NOT NULL,
	from_date DATE NOT NULL,
	to_date DATE NOT NULL,
	commentsFac VARCHAR(1000),
    auth1 VARCHAR(20) NOT NULL,
    auth1Id INT NOT NULL,
    auth1Comments VARCHAR(1000),
	auth1ResponseTime DATE NOT NULL,

	PRIMARY KEY (l_id),
	FOREIGN KEY(l_id) REFERENCES allLeaves(l_id)
);

CREATE TABLE approvedLeaves(
	l_id INT NOT NULL,
	application_date DATE NOT NULL,
	f_id INT NOT NULL,
	d_id VARCHAR(20) NOT NULL,
	from_date DATE NOT NULL,
	to_date DATE NOT NULL,
	commentsFac VARCHAR(1000),
    auth1 VARCHAR(20) NOT NULL,
    auth1Id INT NOT NULL,
    auth1Comments VARCHAR(1000),
	auth1ResponseTime DATE NOT NULL,
    auth2 VARCHAR(20) NOT NULL,
    auth2Id INT NOT NULL,
    auth2Comments VARCHAR(1000),
	auth2ResponseTime DATE NOT NULL,

	PRIMARY KEY (l_id),
	FOREIGN KEY(l_id) REFERENCES allLeaves(l_id)
);