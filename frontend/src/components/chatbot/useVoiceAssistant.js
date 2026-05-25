import { useCallback, useEffect, useRef, useState } from 'react';

/**
 * Browser SpeechRecognition + SpeechSynthesis (free, no API key).
 */
export default function useVoiceAssistant({ onFinalTranscript, speakReplies = true }) {
  const [supported, setSupported] = useState(false);
  const [listening, setListening] = useState(false);
  const [transcript, setTranscript] = useState('');
  const recognitionRef = useRef(null);

  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    setSupported(Boolean(SpeechRecognition && window.speechSynthesis));

    if (!SpeechRecognition) return;

    const recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.interimResults = true;
    recognition.lang = 'en-IN';

    recognition.onresult = (event) => {
      let interim = '';
      let finalText = '';
      for (let i = event.resultIndex; i < event.results.length; i += 1) {
        const t = event.results[i][0].transcript;
        if (event.results[i].isFinal) finalText += t;
        else interim += t;
      }
      setTranscript(finalText || interim);
      if (finalText && onFinalTranscript) {
        onFinalTranscript(finalText.trim());
      }
    };

    recognition.onend = () => setListening(false);
    recognition.onerror = () => setListening(false);

    recognitionRef.current = recognition;

    return () => {
      recognition.stop();
    };
  }, [onFinalTranscript]);

  const startListening = useCallback(() => {
    if (!recognitionRef.current) return;
    setTranscript('');
    setListening(true);
    try {
      recognitionRef.current.start();
    } catch {
      setListening(false);
    }
  }, []);

  const stopListening = useCallback(() => {
    recognitionRef.current?.stop();
    setListening(false);
  }, []);

  const speak = useCallback(
    (text) => {
      if (!speakReplies || !window.speechSynthesis || !text) return;
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.rate = 1;
      utterance.pitch = 1;
      window.speechSynthesis.speak(utterance);
    },
    [speakReplies]
  );

  return {
    supported,
    listening,
    transcript,
    startListening,
    stopListening,
    speak,
    setTranscript,
  };
}
