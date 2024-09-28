const apiBaseUrl = 'http://localhost:8080/users';

document.getElementById('userForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const user = {
        username: username,
        password: password,
        isActive: true
    };

    try {
        const response = await fetch(apiBaseUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user),
        });

        if (response.ok) {
            alert('User created successfully');
            document.getElementById('userForm').reset();
            loadUsers();
        } else {
            alert('Error creating user, username might be taken');
        }
    } catch (error) {
        console.error('Error:', error);
    }
});

async function loadUsers() {
    try {
        const response = await fetch(apiBaseUrl);
        const users = await response.json();
        const usersTable = document.getElementById('usersTable').getElementsByTagName('tbody')[0];

        usersTable.innerHTML = '';

        users.forEach(user => {
            const row = usersTable.insertRow();

            const usernameCell = row.insertCell(0);
            usernameCell.textContent = user.username;

            const isActiveCell = row.insertCell(1);
            isActiveCell.textContent = user.active ? 'Yes' : 'No';

            const actionsCell = row.insertCell(2);
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Delete';
            deleteButton.style.backgroundColor = '#dc3545';
            deleteButton.style.color = 'white';
            deleteButton.style.border = 'none';
            deleteButton.style.padding = '5px 10px';
            deleteButton.style.cursor = 'pointer';
            deleteButton.addEventListener('click', function () {
                deleteUser(user.id);
            });
            actionsCell.appendChild(deleteButton);
        });
    } catch (error) {
        console.error('Error:', error);
    }
}

async function deleteUser(id) {
    if (confirm('Are you sure you want to delete this user?')) {
        try {
            const response = await fetch(`${apiBaseUrl}/${id}`, {
                method: 'DELETE',
            });

            if (response.ok) {
                alert('User deleted successfully');
                loadUsers();
            } else {
                alert('Error deleting user');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }
}

window.onload = loadUsers;
