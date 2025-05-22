import React, {useEffect, useState} from 'react';
import './AlbumGallery.scss';
import {useUserContext} from '../../context/UserContext';

interface AlbumDTO {
    id: string;
    name: string;
    createdAt: string;
    numberOfPhotos: number;
    photos: string[];
}

interface AlbumGalleryProps {
    userId: string;
    token: string;
}

const AlbumGallery: React.FC<AlbumGalleryProps> = ({userId, token}) => {
    const [albums, setAlbums] = useState<AlbumDTO[]>([]);
    const [error, setError] = useState('');
    const [newAlbumName, setNewAlbumName] = useState('');
    const [selectedAlbum, setSelectedAlbum] = useState<AlbumDTO | null>(null);
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [zoomedImage, setZoomedImage] = useState<string | null>(null);
    const {user: currentUser} = useUserContext();

    const fetchAlbums = async () => {
        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/albums/all/${userId}`, {
                headers: {Authorization: `Bearer ${token}`},
            });
            if (!res.ok) throw new Error('No albums');
            const data: AlbumDTO[] = await res.json();

            const albumsWithPhotos = await Promise.all(
                data.map(async (album) => {
                    try {
                        const photoRes = await fetch(`${import.meta.env.VITE_API_URL}/photos/all/${album.id}`, {
                            headers: {Authorization: `Bearer ${token}`},
                        });
                        const photosData = photoRes.ok ? await photoRes.json() : [];

                        const photos = Array.isArray(photosData)
                            ? photosData
                                .map(photo => typeof photo === 'string'
                                    ? photo
                                    : photo.image
                                        ? `data:image/jpeg;base64,${photo.image}`
                                        : null)
                                .filter((p): p is string => p !== null)
                            : [];

                        return {...album, photos};
                    } catch {
                        return {...album, photos: []};
                    }
                })
            );

            setAlbums(albumsWithPhotos);
        } catch (err) {
            setAlbums([]);
            setError('Nu există albume');
        }
    };

    useEffect(() => {
        fetchAlbums();
    }, [userId, token]);

    const handleAddAlbum = async () => {
        if (!token || !userId || !newAlbumName.trim()) return;

        const albumDTO = {
            userId: userId,
            name: newAlbumName.trim(),
            numberOfPhotos: 0,
            createdAt: new Date().toISOString(),
        };

        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/albums/insert`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(albumDTO),
            });

            if (res.ok) {
                setNewAlbumName('');
                fetchAlbums();
            } else {
                alert('Eroare la adăugarea albumului.');
            }
        } catch {
            alert('Eroare rețea la adăugarea albumului.');
        }
    };

    const handleUploadToAlbum = async (albumId: string) => {
        if (!selectedFile) {
            alert('Selectați o imagine întâi');
            return;
        }

        const formData = new FormData();
        formData.append('image', selectedFile);

        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/photos/${albumId}/uploadImage`, {
                method: 'POST',
                headers: {Authorization: `Bearer ${token}`},
                body: formData,
            });

            if (res.ok) {
                // ✅ Publică automat și ca postare
                const postFormData = new FormData();
                postFormData.append('userId', userId);
                postFormData.append('content', `Imagine adăugată în albumul "${selectedAlbum?.name}"`);
                postFormData.append('image', selectedFile);

                try {
                    const postRes = await fetch(`${import.meta.env.VITE_API_URL}/posts/insert`, {
                        method: 'POST',
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                        body: postFormData,
                    });

                    if (!postRes.ok) {
                        console.warn('Postarea automată nu a fost salvată.');
                    }
                } catch (e) {
                    console.error('Eroare la adăugarea postării automate:', e);
                }

                setSelectedFile(null);
                fetchAlbums();

                if (selectedAlbum?.id === albumId) {
                    const updatedPhotosRes = await fetch(`${import.meta.env.VITE_API_URL}/photos/all/${albumId}`, {
                        headers: {Authorization: `Bearer ${token}`},
                    });

                    const photosData = updatedPhotosRes.ok ? await updatedPhotosRes.json() : [];
                    const photos = Array.isArray(photosData)
                        ? photosData
                            .map(photo => typeof photo === 'string'
                                ? photo
                                : photo.image
                                    ? `data:image/jpeg;base64,${photo.image}`
                                    : null)
                            .filter((p): p is string => p !== null)
                        : [];

                    setSelectedAlbum({...selectedAlbum, photos, numberOfPhotos: photos.length});
                }
            } else {
                alert('Eroare la încărcarea imaginii.');
            }
        } catch {
            alert('Eroare rețea la încărcarea imaginii.');
        }
    };

    const handleDeleteAlbum = async (albumId: string) => {
        if (!window.confirm('Sigur vrei să ștergi acest album?')) return;

        try {
            const res = await fetch(`${import.meta.env.VITE_API_URL}/albums/delete/${albumId}`, {
                method: 'DELETE',
                headers: {Authorization: `Bearer ${token}`},
            });

            if (res.ok) {
                alert('Albumul a fost șters.');
                setSelectedAlbum(null);
                fetchAlbums();
            } else {
                alert('Eroare la ștergerea albumului.');
            }
        } catch {
            alert('Eroare rețea la ștergerea albumului.');
        }
    };

    return (
        <div className="album-gallery">
            {currentUser?.id === userId && (
                <div className="add-album-form">
                    <input
                        type="text"
                        placeholder="Nume album nou"
                        value={newAlbumName}
                        onChange={(e) => setNewAlbumName(e.target.value)}
                    />
                    <button onClick={handleAddAlbum}>Adaugă Album</button>
                </div>
            )}

            {error && <p className="no-albums">{error}</p>}

            <div className="albums-grid">
                {albums.map((album) => (
                    <div
                        key={album.id}
                        className="album-tile"
                        onClick={() => setSelectedAlbum(album)}
                    >
                        <p>{album.name}</p>
                        {album.photos?.[0] && (
                            <div className="album-thumbnail">
                                <img
                                    src={album.photos[0]}
                                    alt={`${album.name} thumbnail`}
                                    onError={(e) => {
                                        (e.target as HTMLImageElement).src = 'placeholder.jpg';
                                    }}
                                />
                            </div>
                        )}
                    </div>
                ))}
            </div>

            {selectedAlbum && (
                <div className="album-modal" onClick={() => setSelectedAlbum(null)}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <button className="close-btn" onClick={() => setSelectedAlbum(null)}>×</button>
                        <h3>{selectedAlbum.name}</h3>
                        <div className="photos-grid">
                            {selectedAlbum.photos?.length ? (
                                selectedAlbum.photos.map((photoUrl, idx) => (
                                    <img
                                        key={idx}
                                        src={photoUrl}
                                        alt={`Poza ${idx + 1}`}
                                        onClick={() => setZoomedImage(photoUrl)}
                                        onError={(e) => {
                                            (e.target as HTMLImageElement).src = 'placeholder.jpg';
                                        }}
                                        style={{
                                            width: 150,
                                            height: 150,
                                            objectFit: 'cover',
                                            borderRadius: 4,
                                            cursor: 'zoom-in',
                                        }}
                                    />
                                ))
                            ) : (
                                <p>Nu există poze în acest album.</p>
                            )}
                        </div>

                        {currentUser?.id === userId && (
                            <div className="upload-section">
                                <input
                                    type="file"
                                    accept="image/*"
                                    onChange={(e) => setSelectedFile(e.target.files?.[0] || null)}
                                />
                                <button
                                    onClick={() => handleUploadToAlbum(selectedAlbum.id)}
                                    disabled={!selectedFile}
                                >
                                    Adaugă poză
                                </button>
                                <button
                                    onClick={() => handleDeleteAlbum(selectedAlbum.id)}
                                    style={{backgroundColor: '#e74c3c', color: 'white'}}
                                >
                                    Șterge Album
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            )}

            {zoomedImage && (
                <div
                    className="zoom-modal"
                    onClick={() => setZoomedImage(null)}
                    style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0,0,0,0.8)',
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        zIndex: 2000,
                        cursor: 'zoom-out',
                    }}
                >
                    <img
                        src={zoomedImage}
                        alt="Zoomed"
                        style={{
                            maxWidth: '90%',
                            maxHeight: '90%',
                            borderRadius: 8,
                            boxShadow: '0 0 20px rgba(255,255,255,0.2)',
                        }}
                    />
                </div>
            )}
        </div>
    );
};

export default AlbumGallery;
