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
    Select,
    MenuItem,
    Chip,
    Snackbar,
    Alert,
    CircularProgress,
    IconButton
} from '@mui/material';
import { Refresh as RefreshIcon } from '@mui/icons-material';
import { collection, getDocs, updateDoc, doc } from 'firebase/firestore';
import { db } from '../firebase/config';

const B2BMatches = () => {
    const [matches, setMatches] = useState([]);
    const [loading, setLoading] = useState(true);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    const fetchMatches = async () => {
        setLoading(true);
        try {
            const querySnapshot = await getDocs(collection(db, 'b2b_matches'));
            const matchList = querySnapshot.docs.map(doc => ({
                id: doc.id,
                ...doc.data()
            }));
            setMatches(matchList);
        } catch (error) {
            console.error("Error fetching B2B matches:", error);
            showSnackbar('Eşleşmeler yüklenirken bir hata oluştu.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMatches();
    }, []);

    const handleStatusChange = async (matchId, newStatus) => {
        try {
            const matchRef = doc(db, 'b2b_matches', matchId);
            await updateDoc(matchRef, {
                status: newStatus
            });
            setMatches(prev => prev.map(m => m.id === matchId ? { ...m, status: newStatus } : m));
            showSnackbar(`Eşleşme durumu "${newStatus}" olarak güncellendi.`, 'success');
        } catch (error) {
            console.error("Error updating match status:", error);
            showSnackbar('Durum güncellenirken bir hata oluştu.', 'error');
        }
    };

    const getStatusColor = (status) => {
        switch (status?.toLowerCase()) {
            case 'approved': return 'success';
            case 'pending': return 'warning';
            case 'cancelled': return 'error';
            default: return 'default';
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    return (
        <Box sx={{ pb: 4 }}>
            <Box sx={{ mb: 4, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                    <Typography variant="h4" fontWeight="800" color="primary.main">
                        B2B Eşleşme Yönetimi
                    </Typography>
                    <Typography variant="body1" color="textSecondary">
                        Firmalar arası otomatik eşleşmeleri ve durumlarını yönetin
                    </Typography>
                </Box>
                <IconButton onClick={fetchMatches} disabled={loading} color="primary">
                    <RefreshIcon />
                </IconButton>
            </Box>

            <TableContainer component={Paper} sx={{ borderRadius: 4, boxShadow: '0 4px 20px rgba(0,0,0,0.05)' }}>
                {loading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}>
                        <CircularProgress />
                    </Box>
                ) : (
                    <Table>
                        <TableHead sx={{ backgroundColor: '#F8F9FA' }}>
                            <TableRow>
                                <TableCell sx={{ fontWeight: 'bold' }}>Firma A</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Firma B</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Eşleşme Skoru</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Mevcut Durum</TableCell>
                                <TableCell align="right" sx={{ fontWeight: 'bold' }}>Durumu Güncelle</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {matches.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} align="center" sx={{ py: 8 }}>
                                        <Typography color="textSecondary">Henüz bir B2B eşleşmesi bulunmuyor.</Typography>
                                    </TableCell>
                                </TableRow>
                            ) : (
                                matches.map((match) => (
                                    <TableRow key={match.id} hover>
                                        <TableCell sx={{ fontWeight: 500 }}>{match.companyAName || match.firmAId || 'Firma A'}</TableCell>
                                        <TableCell sx={{ fontWeight: 500 }}>{match.companyBName || match.firmBId || 'Firma B'}</TableCell>
                                        <TableCell>
                                            <Chip
                                                label={`%${(match.matchScore || 0).toFixed(0)}`}
                                                color={match.matchScore > 80 ? 'success' : 'primary'}
                                                variant="outlined"
                                                size="small"
                                                sx={{ fontWeight: 'bold' }}
                                            />
                                        </TableCell>
                                        <TableCell>
                                            <Chip
                                                label={(match.status || 'pending').toUpperCase()}
                                                color={getStatusColor(match.status)}
                                                size="small"
                                                sx={{ fontWeight: 'bold', fontSize: '0.7rem' }}
                                            />
                                        </TableCell>
                                        <TableCell align="right">
                                            <Select
                                                value={match.status || 'pending'}
                                                onChange={(e) => handleStatusChange(match.id, e.target.value)}
                                                size="small"
                                                sx={{ minWidth: 120, borderRadius: 2 }}
                                            >
                                                <MenuItem value="pending">Beklemede</MenuItem>
                                                <MenuItem value="approved">Onaylandı</MenuItem>
                                                <MenuItem value="cancelled">İptal Edildi</MenuItem>
                                            </Select>
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
                autoHideDuration={4000}
                onClose={() => setSnackbar(prev => ({ ...prev, open: false }))}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
            >
                <Alert severity={snackbar.severity} sx={{ width: '100%', borderRadius: 3 }}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default B2BMatches;
