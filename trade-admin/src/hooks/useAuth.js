import { useState, useEffect } from 'react';
import { auth, db } from '../firebase/config';
import { onAuthStateChanged } from 'firebase/auth';
import { doc, getDoc } from 'firebase/firestore';

/**
 * Hook to get the current authenticated user and their role.
 */
export const useAuth = () => {
    const [user, setUser] = useState(null);
    const [role, setRole] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
            if (firebaseUser) {
                setUser(firebaseUser);
                // Fetch role from Firestore
                try {
                    const userDoc = await getDoc(doc(db, 'users', firebaseUser.uid));
                    if (userDoc.exists()) {
                        setRole(userDoc.data().role);
                    }
                } catch (error) {
                    console.error("Error fetching user role:", error);
                }
            } else {
                setUser(null);
                setRole(null);
            }
            setLoading(false);
        });

        return unsubscribe;
    }, []);

    return { user, role, loading };
};
