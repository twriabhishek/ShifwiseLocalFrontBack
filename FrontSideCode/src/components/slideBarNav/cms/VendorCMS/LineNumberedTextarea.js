import React, { useState } from 'react';
import styled from 'styled-components';

const EditorContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const TextAreaContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
`;

const EditorWrapper = styled.div`
  display: inline-flex;
  gap: 10px;
  font-family: monospace;
  line-height: 21px;
  background: white;
  border-radius: 2px;
  border: 1px solid #ddd;
  padding: 20px 10px;
  height: 200px;
  overflow-y: auto;
`;

const LineNumbersWrapper = styled.div`
  width: 20px;
  text-align: right;
  height: 9999px;
`;

const LineNumber = styled.span`
  counter-increment: linenumber;
  color: #506882;
`;

const Textarea = styled.textarea`
  height: 9999px;
  line-height: 21px;
  overflow-y: hidden;
  padding: 0;
  border: 0;
  background: white;
  color: #282a3a;
  min-width: 500px;
  outline: none;
  resize: none;
`;



const LineNumbersTextarea = ({ value, onChange }) => {
  const [text, setText] = useState(value);

  const handleKeyUp = (event) => {
    const numberOfLines = event.target.value.split('\n').length;
    const lineNumbers = Array.from(Array(numberOfLines).keys()).map((index) => (
      <LineNumber key={index}>{index + 1}. </LineNumber>
    ));

    setLineNumbers(lineNumbers);
    setText(event.target.value);
    onChange(event.target.value); // Update the template state using the passed onChange prop
  };

  const handleFormatJson = () => {
    try {
      const formattedJson = JSON.stringify(JSON.parse(text), null, 2);
      setText(formattedJson);
      onChange(formattedJson); // Update the template state with formatted JSON
    } catch (error) {
      console.error('Invalid JSON format:', error);
    }
  };

  const handleKeyDown = (event) => {
    if (event.key === 'Tab') {
      const { selectionStart, selectionEnd } = event.target;
      const newText =
        text.substring(0, selectionStart) + '\t' + text.substring(selectionEnd);

      setText(newText);
      onChange(newText); // Update the template state with the updated text
      event.preventDefault();
    }
  };

  const [lineNumbers, setLineNumbers] = useState([]);

  return (
    <EditorContainer>
      <TextAreaContainer>
        <EditorWrapper>
          <LineNumbersWrapper>{lineNumbers}</LineNumbersWrapper>
          <Textarea
            value={text}
            onChange={(e) => handleKeyUp(e)}
            onKeyUp={handleKeyUp}
            onKeyDown={handleKeyDown}
          ></Textarea>
        </EditorWrapper>
      </TextAreaContainer>
      <button className="btn buttonStyling" onClick={handleFormatJson}>Format JSON</button>
    </EditorContainer>
  );
};

export default LineNumbersTextarea;