import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Button,
    Chip,
    Snackbar,
    Alert,
    CircularProgress,
    IconButton,
    Tooltip
} from '@mui/material';
import {
    CheckCircle as ApproveIcon,
    Cancel as RejectIcon,
    Refresh as RefreshIcon
} from '@mui/icons-material';
import {
    collection,
    query,
    where,
    getDocs,
    updateDoc,
    doc,
    orderBy
} from 'firebase/firestore';
import { db } from '../firebase/config';
import { formatDate } from '../utils/helpers';

const Users = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
    const [actionLoading, setActionLoading] = useState(null);

    const fetchPendingUsers = async () => {
        setLoading(true);
        try {
            const q = query(
                collection(db, 'users'),
                where('status', '==', 'pending')
            );
            const querySnapshot = await getDocs(q);
            const userList = querySnapshot.docs.map(doc => ({
                id: doc.id,
                ...doc.data()
            }));
            // Sortering by createdAt if exists, otherwise by email
            userList.sort((a, b) => (b.createdAt || 0) - (a.createdAt || 0));
            setUsers(userList);
        } catch (error) {
            console.error("Error fetching pending users:", error);
            showSnackbar('Kullanıcılar yüklenirken bir hata oluştu.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchPendingUsers();
    }, []);

    const handleAction = async (userId, action) => {
        setActionLoading(userId);
        try {
            const userRef = doc(db, 'users', userId);
            if (action === 'approve') {
                await updateDoc(userRef, {
                    status: 'approved',
                    role: 'company',
                    approvedAt: Date.now()
                });
                showSnackbar('Kullanıcı başarıyla onaylandı ve "Şirket" rolü atandı.', 'success');
            } else {
                await updateDoc(userRef, {
                    status: 'rejected',
                    rejectedAt: Date.now()
                });
                showSnackbar('Kullanıcı başvurusu reddedildi.', 'warning');
            }
            // Remove from local state
            setUsers(prev => prev.filter(u => u.id !== userId));
        } catch (error) {
            console.error(`Error during ${action}:`, error);
            showSnackbar('İşlem sırasında bir hata oluştu.', 'error');
        } finally {
            setActionLoading(null);
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleCloseSnackbar = () => {
        setSnackbar(prev => ({ ...prev, open: false }));
    };

    return (
        <Box sx={{ pb: 4 }}>
            <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                    <Typography variant="h4" fontWeight="800" color="primary.main">
                        Kullanıcı Onayları
                    </Typography>
                    <Typography variant="body1" color="textSecondary">
                        Onay bekleyen şirket hesaplarını yönetin
                    </Typography>
                </Box>
                <Tooltip title="Listeyi Yenile">
                    <IconButton onClick={fetchPendingUsers} disabled={loading} color="primary">
                        <RefreshIcon />
                    </IconButton>
                </Tooltip>
            </Box>

            <TableContainer component={Paper} sx={{ borderRadius: 4, boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
                {loading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}>
                        <CircularProgress />
                    </Box>
                ) : (
                    <Table sx={{ minWidth: 650 }}>
                        <TableHead sx={{ backgroundColor: '#F8F9FA' }}>
                            <TableRow>
                                <TableCell sx={{ fontWeight: 'bold' }}>E-posta</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Firma ID</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Kayıt Tarihi</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Durum</TableCell>
                                <TableCell align="right" sx={{ fontWeight: 'bold' }}>İşlemler</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {users.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} align="center" sx={{ py: 8 }}>
                                        <Typography color="textSecondary">Onay bekleyen kullanıcı bulunamadı.</Typography>
                                    </TableCell>
                                </TableRow>
                            ) : (
                                users.map((user) => (
                                    <TableRow key={user.id} hover>
                                        <TableCell fontWeight="500">{user.email}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={user.firmId || 'Atanmamış'}
                                                size="small"
                                                variant="outlined"
                                                sx={{ borderRadius: 1 }}
                                            />
                                        </TableCell>
                                        <TableCell color="textSecondary">
                                            {user.createdAt ? formatDate(user.createdAt) : '—'}
                                        </TableCell>
                                        <TableCell>
                                            <Chip
                                                label="BEKLEYEN"
                                                size="small"
                                                color="warning"
                                                sx={{ fontWeight: 'bold', fontSize: '0.65rem' }}
                                            />
                                        </TableCell>
                                        <TableCell align="right">
                                            <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                                                <Button
                                                    variant="contained"
                                                    color="success"
                                                    size="small"
                                                    startIcon={<ApproveIcon />}
                                                    onClick={() => handleAction(user.id, 'approve')}
                                                    disabled={actionLoading === user.id}
                                                    sx={{ borderRadius: 2 }}
                                                >
                                                    Onayla
                                                </Button>
                                                <Button
                                                    variant="outlined"
                                                    color="error"
                                                    size="small"
                                                    startIcon={<RejectIcon />}
                                                    onClick={() => handleAction(user.id, 'reject')}
                                                    disabled={actionLoading === user.id}
                                                    sx={{ borderRadius: 2 }}
                                                >
                                                    Reddet
                                                </Button>
                                            </Box>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                )}
            </TableContainer>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={handleCloseSnackbar}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
            >
                <Alert onClose={handleCloseSnackbar} severity={snackbar.severity} sx={{ width: '100%', borderRadius: 3 }}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Users;
