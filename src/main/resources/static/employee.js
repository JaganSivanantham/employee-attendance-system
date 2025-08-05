document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('authToken');
    if (!token || localStorage.getItem('userRole') !== 'EMPLOYEE') {
        window.location.href = 'login.html';
        return;
    }

    const welcomeMessage = document.getElementById('welcome-message');
    const statusMessage = document.getElementById('status-message');
    const checkinBtn = document.getElementById('checkin-btn');
    const checkoutBtn = document.getElementById('checkout-btn');
    const logoutBtn = document.getElementById('logout-btn');
    const historyTableBody = document.querySelector('#history-table tbody');

    const employeeName = localStorage.getItem('employeeName');
    const employeeId = localStorage.getItem('employeeId');
    welcomeMessage.textContent = `Welcome, ${employeeName}!`;

    const handleAttendance = async (action) => {
        statusMessage.textContent = 'Getting your location...';
        navigator.geolocation.getCurrentPosition(async (position) => {
            const { latitude, longitude } = position.coords;
            statusMessage.textContent = `Location acquired. Performing ${action}...`;

            try {
                const response = await fetch(`/api/attendance/${action}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({ latitude, longitude })
                });

                const message = await response.text();
                statusMessage.textContent = message;
                if(response.ok) {
                    loadAttendanceHistory();
                }

            } catch (error) {
                statusMessage.textContent = `Error during ${action}.`;
                console.error(error);
            }

        }, (error) => {
            statusMessage.textContent = 'Could not get location. Please enable location services.';
            console.error('Geolocation Error:', error);
        });
    };

    const loadAttendanceHistory = async () => {
        try {
            const response = await fetch(`/api/attendance/history/${employeeId}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const records = await response.json();
                historyTableBody.innerHTML = ''; // Clear existing
                records.forEach(record => {
                    const row = `<tr>
                        <td>${record.date}</td>
                        <td>${record.checkInTime ? new Date(record.checkInTime).toLocaleTimeString() : 'N/A'}</td>
                        <td>${record.checkOutTime ? new Date(record.checkOutTime).toLocaleTimeString() : 'N/A'}</td>
                        <td>${record.status}</td>
                    </tr>`;
                    historyTableBody.innerHTML += row;
                });
            }
        } catch (error) {
            console.error('Failed to load history:', error);
        }
    };


    checkinBtn.addEventListener('click', () => handleAttendance('check-in'));
    checkoutBtn.addEventListener('click', () => handleAttendance('check-out'));
    logoutBtn.addEventListener('click', () => {
        localStorage.clear();
        window.location.href = 'login.html';
    });

    // Initial load
    loadAttendanceHistory();
});