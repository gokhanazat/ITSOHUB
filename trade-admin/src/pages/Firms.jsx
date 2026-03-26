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
    IconButton,
    Switch,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Chip,
    Snackbar,
    Alert,
    CircularProgress
} from '@mui/material';
import { Edit as EditIcon, Refresh as RefreshIcon } from '@mui/icons-material';
import { collection, getDocs, updateDoc, doc } from 'firebase/firestore';
import { db } from '../firebase/config';

const Firms = () => {
    const [firms, setFirms] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [currentFirm, setCurrentFirm] = useState(null);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    const fetchFirms = async () => {
        setLoading(true);
        try {
            const querySnapshot = await getDocs(collection(db, 'firms'));
            const firmList = querySnapshot.docs.map(doc => ({
                id: doc.id,
                ...doc.data()
            }));
            setFirms(firmList);
        } catch (error) {
            console.error("Error fetching firms:", error);
            showSnackbar('Firmalar yüklenirken bir hata oluştu.', 'error');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchFirms();
    }, []);

    const handleToggleApproved = async (firmId, currentStatus) => {
        try {
            const firmRef = doc(db, 'firms', firmId);
            await updateDoc(firmRef, {
                isApproved: !currentStatus
            });
            setFirms(prev => prev.map(f => f.id === firmId ? { ...f, isApproved: !currentStatus } : f));
            showSnackbar('Firma onay durumu güncellendi.', 'success');
        } catch (error) {
            console.error("Error toggling approval:", error);
            showSnackbar('Güncelleme sırasında bir hata oluştu.', 'error');
        }
    };

    const handleEditClick = (firm) => {
        setCurrentFirm({ ...firm });
        setEditDialogOpen(true);
    };

    const handleSaveEdit = async () => {
        if (!currentFirm) return;
        try {
            const firmRef = doc(db, 'firms', currentFirm.id);
            await updateDoc(firmRef, {
                exportCapacity: currentFirm.exportCapacity || '',
                targetMarkets: currentFirm.targetMarkets || ''
            });
            setFirms(prev => prev.map(f => f.id === currentFirm.id ? { ...currentFirm } : f));
            setEditDialogOpen(false);
            showSnackbar('Firma bilgileri başarıyla güncellendi.', 'success');
        } catch (error) {
            console.error("Error updating firm info:", error);
            showSnackbar('Güncelleme başarısız oldu.', 'error');
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
                        Firma Yönetimi
                    </Typography>
                    <Typography variant="body1" color="textSecondary">
                        Kayıtlı firmaları görüntüleyin ve yönetin
                    </Typography>
                </Box>
                <IconButton onClick={fetchFirms} disabled={loading} color="primary">
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
                                <TableCell sx={{ fontWeight: 'bold' }}>Firma Adı</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Ülke</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Sektör</TableCell>
                                <TableCell sx={{ fontWeight: 'bold' }}>Onaylı</TableCell>
                                <TableCell align="right" sx={{ fontWeight: 'bold' }}>İşlemler</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {firms.length === 0 ? (
                                <TableRow>
                                    <TableCell colSpan={5} align="center" sx={{ py: 8 }}>
                                        <Typography color="textSecondary">Kayıtlı firma bulunamadı.</Typography>
                                    </TableCell>
                                </TableRow>
                            ) : (
                                firms.map((firm) => (
                                    <TableRow key={firm.id} hover>
                                        <TableCell sx={{ fontWeight: 500 }}>{firm.name}</TableCell>
                                        <TableCell>{firm.country || '—'}</TableCell>
                                        <TableCell>
                                            <Chip label={firm.sector || 'Genel'} size="small" variant="outlined" />
                                        </TableCell>
                                        <TableCell>
                                            <Switch
                                                checked={!!firm.isApproved}
                                                onChange={() => handleToggleApproved(firm.id, !!firm.isApproved)}
                                                color="success"
                                            />
                                        </TableCell>
                                        <TableCell align="right">
                                            <IconButton onClick={() => handleEditClick(firm)} color="primary">
                                                <EditIcon />
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))
                            )}
                        </TableBody>
                    </Table>
                )}
            </TableContainer>

            {/* Edit Dialog */}
            <Dialog open={editDialogOpen} onClose={() => setEditDialogOpen(false)} fullWidth maxWidth="sm">
                <DialogTitle sx={{ fontWeight: 'bold' }}>Firma Bilgilerini Düzenle</DialogTitle>
                <DialogContent>
                    <Box sx={{ pt: 2, display: 'flex', flexDirection: 'column', gap: 3 }}>
                        <TextField
                            label="Firma Adı"
                            value={currentFirm?.name || ''}
                            disabled
                            fullWidth
                        />
                        <TextField
                            label="İhracat Kapasitesi"
                            value={currentFirm?.exportCapacity || ''}
                            onChange={(e) => setCurrentFirm(prev => ({ ...prev, exportCapacity: e.target.value }))}
                            placeholder="Örn: 1.000.000 $"
                            fullWidth
                        />
                        <TextField
                            label="Hedef Pazarlar"
                            value={currentFirm?.targetMarkets || ''}
                            onChange={(e) => setCurrentFirm(prev => ({ ...prev, targetMarkets: e.target.value }))}
                            placeholder="Örn: Avrupa, Ortadoğu"
                            fullWidth
                            multiline
                            rows={3}
                        />
                    </Box>
                </DialogContent>
                <DialogActions sx={{ p: 3 }}>
                    <Button onClick={() => setEditDialogOpen(false)}>İptal</Button>
                    <Button onClick={handleSaveEdit} variant="contained" sx={{ borderRadius: 2 }}>
                        Değişiklikleri Kaydet
                    </Button>
                </DialogActions>
            </Dialog>

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

export default Firms;
