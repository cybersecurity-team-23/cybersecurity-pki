INSERT INTO requests (issuer_serial_number, common_name, surname, given_name, organisation, organisational_unit, country, email, type, status) VALUES
(123456789, 'John Doe', 'Doe', 'John', 'Example Corp', 'IT Department', 'US', 'john.doe@example.com', 'ROOT', 'APPROVED'),
(987654321, 'Alice Johnson', 'Johnson', 'Alice', 'Example Corp', 'HR Department', 'CA', 'alice.johnson@example.com', 'INTERMEDIATE', 'PENDING'),
    (192837465, 'Bob Smith', 'Smith', 'Bob', 'Example Corp', 'Finance Department', 'GB', 'bob.smith@example.com', 'END_ENTITY', 'REJECTED');
